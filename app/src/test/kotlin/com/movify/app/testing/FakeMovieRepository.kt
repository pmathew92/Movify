package com.movify.app.testing

import com.movify.app.data.repository.MovieRepository
import com.movify.app.models.Result
import kotlinx.coroutines.awaitCancellation

class FakeMovieRepository : MovieRepository {
    var discoveryCalls = 0
        private set
    var activeDiscoveryRequests = 0
        private set
    var activeSearchRequests = 0
        private set
    var discoveryResponse: suspend () -> List<Result> = { awaitCancellation() }
    val searchQueries = mutableListOf<String>()
    val searchResponses = mutableMapOf<String, suspend () -> List<Result>>()

    override suspend fun getMovies(): List<Result> {
        discoveryCalls++
        activeDiscoveryRequests++
        try {
            return discoveryResponse()
        } finally {
            activeDiscoveryRequests--
        }
    }

    override suspend fun searchMovies(query: String): List<Result> {
        searchQueries += query
        activeSearchRequests++
        try {
            return searchResponses[query]?.invoke().orEmpty()
        } finally {
            activeSearchRequests--
        }
    }
}
