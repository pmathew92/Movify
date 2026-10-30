package com.movify.app.ui.search

import androidx.lifecycle.ViewModelStore
import com.movify.app.models.Result
import com.movify.app.testing.FakeMovieRepository
import com.movify.app.testing.MainDispatcherRule
import com.movify.app.ui.common.MovieListUiState
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private val repository = FakeMovieRepository()
    private val store = ViewModelStore()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        viewModel = SearchViewModel(repository, mainDispatcherRule.dispatcher)
        store.put("search", viewModel)
    }

    @After
    fun tearDown() {
        store.clear()
    }

    @Test
    fun blankQueriesDoNotMakeRequests() = runTest(mainDispatcherRule.dispatcher) {
        viewModel.setQuery("   ")
        advanceTimeBy(1000)
        runCurrent()
        assertTrue(repository.searchQueries.isEmpty())
        assertEquals(MovieListUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun typingIsDebouncedAndTheQueryIsTrimmed() = runTest(mainDispatcherRule.dispatcher) {
        viewModel.setQuery("star")
        advanceTimeBy(299)
        assertTrue(repository.searchQueries.isEmpty())
        assertEquals(MovieListUiState.Loading, viewModel.uiState.value)

        viewModel.setQuery("  star wars  ")
        advanceTimeBy(300)
        runCurrent()
        assertEquals(listOf("star wars"), repository.searchQueries)
        assertEquals("star wars", viewModel.query)
        assertEquals(MovieListUiState.Success(emptyList()), viewModel.uiState.value)
    }

    @Test
    fun identicalQueriesDoNotRefetch() = runTest(mainDispatcherRule.dispatcher) {
        viewModel.setQuery("movie")
        advanceTimeBy(300)
        runCurrent()
        viewModel.setQuery(" movie ")
        advanceTimeBy(300)
        runCurrent()
        assertEquals(1, repository.searchQueries.size)
    }

    @Test
    fun newQueriesCancelStaleRequestsBeforeTheNextDebounceFinishes() = runTest(mainDispatcherRule.dispatcher) {
        val first = CompletableDeferred<List<Result>>()
        val second = CompletableDeferred<List<Result>>()
        repository.searchResponses["first"] = { first.await() }
        repository.searchResponses["second"] = { second.await() }
        viewModel.setQuery("first")
        advanceTimeBy(300)
        runCurrent()
        assertEquals(1, repository.activeSearchRequests)

        viewModel.setQuery("second")
        runCurrent()
        assertEquals(0, repository.activeSearchRequests)
        first.complete(listOf(Result(id = 1, title = "Stale")))
        runCurrent()
        assertEquals(MovieListUiState.Loading, viewModel.uiState.value)
        advanceTimeBy(300)
        runCurrent()
        second.complete(listOf(Result(id = 2, title = "Current")))
        runCurrent()
        assertEquals(
            MovieListUiState.Success(listOf(Result(id = 2, title = "Current"))),
            viewModel.uiState.value,
        )
    }

    @Test
    fun clearingTheQueryCancelsPendingWorkAndClearsResults() = runTest(mainDispatcherRule.dispatcher) {
        val response = CompletableDeferred<List<Result>>()
        repository.searchResponses["movie"] = { response.await() }
        viewModel.setQuery("movie")
        advanceTimeBy(300)
        runCurrent()
        assertEquals(1, repository.activeSearchRequests)

        viewModel.setQuery("")
        runCurrent()
        assertEquals(0, repository.activeSearchRequests)
        response.complete(listOf(Result(id = 1, title = "Stale")))
        runCurrent()
        assertEquals(MovieListUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun errorsDoNotPreventFutureSearches() = runTest(mainDispatcherRule.dispatcher) {
        repository.searchResponses["offline"] = { throw IOException("offline") }
        repository.searchResponses["online"] = { listOf(Result(id = 2, title = "Found")) }
        viewModel.setQuery("offline")
        advanceTimeBy(300)
        runCurrent()
        assertTrue(viewModel.uiState.value is MovieListUiState.Error)

        viewModel.setQuery("online")
        advanceTimeBy(300)
        runCurrent()
        assertEquals(
            MovieListUiState.Success(listOf(Result(id = 2, title = "Found"))),
            viewModel.uiState.value,
        )
    }

    @Test
    fun theSameQueryCanBeRetriedAfterAnError() = runTest(mainDispatcherRule.dispatcher) {
        repository.searchResponses["movie"] = { throw IOException("offline") }
        viewModel.setQuery("movie")
        advanceTimeBy(300)
        runCurrent()
        assertTrue(viewModel.uiState.value is MovieListUiState.Error)

        repository.searchResponses["movie"] = { emptyList() }
        viewModel.retry()
        advanceTimeBy(300)
        runCurrent()
        assertEquals(2, repository.searchQueries.size)
        assertEquals(MovieListUiState.Success(emptyList()), viewModel.uiState.value)
    }

    @Test
    fun clearingTheViewModelCancelsTheDebounceTimer() = runTest(mainDispatcherRule.dispatcher) {
        viewModel.setQuery("movie")
        runCurrent()
        store.clear()
        advanceTimeBy(1000)
        runCurrent()
        assertTrue(repository.searchQueries.isEmpty())
    }

    @Test
    fun clearingTheViewModelCancelsInFlightRequestsWithoutAnError() = runTest(mainDispatcherRule.dispatcher) {
        repository.searchResponses["movie"] = { kotlinx.coroutines.awaitCancellation() }
        viewModel.setQuery("movie")
        advanceTimeBy(300)
        runCurrent()
        assertEquals(1, repository.activeSearchRequests)

        store.clear()
        runCurrent()
        assertEquals(0, repository.activeSearchRequests)
        assertEquals(MovieListUiState.Loading, viewModel.uiState.value)
    }
}
