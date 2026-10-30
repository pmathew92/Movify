package com.movify.app.ui.main

import androidx.lifecycle.ViewModelStore
import com.movify.app.models.Result
import com.movify.app.testing.FakeMovieRepository
import com.movify.app.testing.MainDispatcherRule
import com.movify.app.ui.common.MovieListUiState.Status
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private val repository = FakeMovieRepository()
    private val store = ViewModelStore()

    @After
    fun tearDown() {
        store.clear()
    }

    @Test
    fun initialLoadMovesFromLoadingToSuccess() = runTest(mainDispatcherRule.dispatcher) {
        val response = CompletableDeferred<List<Result>>()
        repository.discoveryResponse = { response.await() }
        val viewModel = createViewModel()
        assertEquals(Status.LOADING, viewModel.uiState.value.status)
        runCurrent()

        response.complete(listOf(Result(id = 42, title = "Test Movie")))
        runCurrent()

        assertEquals(1, repository.discoveryCalls)
        assertEquals(Status.SUCCESS, viewModel.uiState.value.status)
        assertEquals("Test Movie", viewModel.uiState.value.movies.single().title)
    }

    @Test
    fun errorsStopLoadingAndCanBeRetried() = runTest(mainDispatcherRule.dispatcher) {
        val failure = IOException("offline")
        repository.discoveryResponse = { throw failure }
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(Status.ERROR, viewModel.uiState.value.status)
        assertTrue(viewModel.uiState.value.error is IOException)
        assertEquals(failure.message, viewModel.uiState.value.error?.message)

        repository.discoveryResponse = { emptyList() }
        viewModel.loadMovies()
        runCurrent()

        assertEquals(2, repository.discoveryCalls)
        assertEquals(Status.SUCCESS, viewModel.uiState.value.status)
        assertTrue(viewModel.uiState.value.movies.isEmpty())
    }

    @Test
    fun reloadingCancelsThePreviousRequest() = runTest(mainDispatcherRule.dispatcher) {
        val first = CompletableDeferred<List<Result>>()
        val second = CompletableDeferred<List<Result>>()
        repository.discoveryResponse = { first.await() }
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(1, repository.activeDiscoveryRequests)

        repository.discoveryResponse = { second.await() }
        viewModel.loadMovies()
        runCurrent()
        assertEquals(1, repository.activeDiscoveryRequests)
        first.complete(listOf(Result(id = 1, title = "Stale")))
        runCurrent()
        assertEquals(Status.LOADING, viewModel.uiState.value.status)

        second.complete(listOf(Result(id = 2, title = "Current")))
        runCurrent()
        assertEquals("Current", viewModel.uiState.value.movies.single().title)
    }

    @Test
    fun clearingTheViewModelCancelsWithoutPublishingAnError() = runTest(mainDispatcherRule.dispatcher) {
        val response = CompletableDeferred<List<Result>>()
        repository.discoveryResponse = { response.await() }
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(1, repository.activeDiscoveryRequests)

        store.clear()
        runCurrent()
        assertEquals(0, repository.activeDiscoveryRequests)
        response.complete(emptyList())
        runCurrent()
        assertEquals(Status.LOADING, viewModel.uiState.value.status)
    }

    @Test
    fun resultsRemainAvailableWithoutAnActiveCollector() = runTest(mainDispatcherRule.dispatcher) {
        repository.discoveryResponse = { listOf(Result(id = 42, title = "Retained")) }
        val viewModel = createViewModel()
        runCurrent()

        val latestState = viewModel.uiState.first()
        assertEquals(Status.SUCCESS, latestState.status)
        assertEquals("Retained", latestState.movies.single().title)
        assertEquals(1, repository.discoveryCalls)
    }

    private fun createViewModel() = MainViewModel(repository, mainDispatcherRule.dispatcher).also {
        store.put("main", it)
    }
}
