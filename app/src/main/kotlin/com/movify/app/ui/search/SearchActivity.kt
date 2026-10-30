package com.movify.app.ui.search

import android.app.SearchManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.movify.app.MyApplication
import com.movify.app.R
import com.movify.app.adapters.MoviesAdapter
import com.movify.app.databinding.ActivitySearchBinding
import com.movify.app.ui.common.MovieListUiState
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: MoviesAdapter
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                    or WindowInsetsCompat.Type.ime(),
            )
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)

        setSupportActionBar(binding.toolbar.root)
        adapter = MoviesAdapter()
        binding.rvQueryResult.layoutManager = LinearLayoutManager(this)
        binding.rvQueryResult.adapter = adapter

        val factory = (application as MyApplication).appContainer.viewModelFactory
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]
        binding.retryButton.setOnClickListener { viewModel.retry() }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: MovieListUiState) {
        val loading = state.status == MovieListUiState.Status.LOADING
        val error = state.status == MovieListUiState.Status.ERROR
        val hasMovies = state.movies.isNotEmpty()
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.rvQueryResult.visibility = if (hasMovies) View.VISIBLE else View.GONE
        binding.stateContainer.visibility = if (!loading && !hasMovies) View.VISIBLE else View.GONE
        binding.retryButton.visibility = if (error) View.VISIBLE else View.GONE
        val message = if (state.status == MovieListUiState.Status.IDLE) {
            R.string.search_prompt
        } else {
            R.string.no_search_results
        }
        binding.tvStatus.setText(if (error) R.string.error_loading_movies else message)
        adapter.submitList(state.movies)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        val searchManager = getSystemService(SEARCH_SERVICE) as? SearchManager
        searchManager?.let {
            searchView.setSearchableInfo(it.getSearchableInfo(componentName))
        }
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setQuery(viewModel.query, false)
        if (viewModel.query.isNotEmpty()) {
            searchView.isIconified = false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.setQuery(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.setQuery(query)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        item.itemId == R.id.action_search || super.onOptionsItemSelected(item)
}
