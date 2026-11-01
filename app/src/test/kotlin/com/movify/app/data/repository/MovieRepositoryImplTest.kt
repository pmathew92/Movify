package com.movify.app.data.repository

import com.movify.app.models.MovieResponse
import com.movify.app.models.Result
import com.movify.app.network.NetworkInterface
import java.io.IOException
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.ContinuationInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class MovieRepositoryImplTest {
    private val service = FakeService()
    private val dispatcher = StandardTestDispatcher()
    private val repository: MovieRepository = MovieRepositoryImpl(service, "test-key", dispatcher)

    @Test
    fun discoveryMapsMovieListAndSuppliesApiKey() = runTest(dispatcher) {
        val movies = mutableListOf(Result(id = 42, title = "Test Movie"))
        service.response = { MovieResponse(1, 1, 1, movies) }
        val actual = repository.getMovies()

        assertEquals("test-key", service.apiKey)
        assertEquals("Test Movie", actual.single().title)
        movies.clear()
        assertEquals(1, actual.size)
        assertThrows(UnsupportedOperationException::class.java) { (actual as MutableList).clear() }
    }

    @Test
    fun searchForwardsQueryAndMapsResponse() = runTest(dispatcher) {
        service.response = { MovieResponse(results = listOf(Result(id = 7, title = "Wall-E"))) }
        val actual = repository.searchMovies("Wall-E & friends")

        assertEquals(7, actual.single().id)
        assertEquals("test-key", service.apiKey)
        assertEquals("Wall-E & friends", service.query)
    }

    @Test
    fun missingResultsBecomeAnEmptyList() = runTest(dispatcher) {
        service.response = { MovieResponse() }
        assertTrue(repository.getMovies().isEmpty())
        assertTrue(repository.searchMovies("missing").isEmpty())
    }

    @Test
    fun networkFailuresPropagateToTheCaller() = runTest(dispatcher) {
        val failure = IOException("offline")
        service.response = { throw failure }
        for (request in listOf<suspend () -> List<Result>>(
            { repository.getMovies() }, { repository.searchMovies("test") },
        )) {
            try {
                request()
                fail("Expected the network failure")
            } catch (actual: IOException) {
                assertEquals(failure.javaClass, actual.javaClass)
                assertEquals(failure.message, actual.message)
            }
        }
    }

    @Test
    fun bothRequestsUseTheInjectedDispatcher() = runTest {
        val repositoryDispatcher = StandardTestDispatcher(testScheduler)
        service.response = {
            assertSame(repositoryDispatcher, currentCoroutineContext()[ContinuationInterceptor])
            MovieResponse()
        }
        val repository = MovieRepositoryImpl(service, "test-key", repositoryDispatcher)
        repository.getMovies()
        repository.searchMovies("test")
    }

    private class FakeService : NetworkInterface {
        var response: suspend () -> MovieResponse = { MovieResponse() }
        var apiKey: String? = null
        var query: String? = null

        override suspend fun getMovies(apiKey: String): MovieResponse {
            this.apiKey = apiKey
            return response()
        }

        override suspend fun getMoviesBasedOnQuery(apiKey: String, query: String): MovieResponse {
            this.apiKey = apiKey
            this.query = query
            return response()
        }
    }
}
