package com.movify.app.network

import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class NetworkInterfaceTest {
    private val request = AtomicReference<Request>()
    private lateinit var service: NetworkInterface
    private lateinit var client: OkHttpClient
    @Volatile private var statusCode = 200

    @Before
    fun setUp() {
        client = OkHttpClient.Builder().addInterceptor { chain ->
            request.set(chain.request())
            val json = """{"page":1,"total_results":1,"total_pages":1,
                "results":[{"id":42,"title":"Test Movie","vote_average":7.5,
                "poster_path":"/poster.jpg","release_date":"2026-10-30"}]}"""
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(statusCode)
                .message(if (statusCode == 200) "OK" else "Unauthorized")
                .body(json.toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        service = NetworkClient.retrofit.newBuilder().client(client).build()
            .create(NetworkInterface::class.java)
    }

    @After
    fun tearDown() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }

    @Test
    fun discoveryDeserializesThroughTheSuspendApi() = runTest {
        val response = service.getMovies("test-key")
        assertEquals(1, response.totalResults)
        val movie = response.results!!.single()
        assertEquals(42, movie.id)
        assertEquals("Test Movie", movie.title)
        assertEquals(7.5f, movie.voteAverage)
        assertEquals("/poster.jpg", movie.posterPath)
        assertEquals("2026-10-30", movie.releaseDate)
        assertEquals("/3/discover/movie", request.get().url.encodedPath)
        assertEquals("test-key", request.get().url.queryParameter("api_key"))
        assertTrue(request.get().url.isHttps)
    }

    @Test
    fun searchPreservesQueryParameters() = runTest {
        service.getMoviesBasedOnQuery("test-key", "Wall-E & friends")
        assertEquals("/3/search/movie", request.get().url.encodedPath)
        assertEquals("Wall-E & friends", request.get().url.queryParameter("query"))
    }

    @Test
    fun httpErrorsReachTheSuspendingCaller() = runTest {
        statusCode = 401
        try {
            service.getMovies("invalid-key")
            fail("Expected HTTP 401")
        } catch (error: HttpException) {
            assertEquals(401, error.code())
        }
    }
}
