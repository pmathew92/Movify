package com.movify.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import com.movify.app.databinding.ActivityMainBinding
import com.movify.app.ui.common.MovieListUiState
import com.movify.app.ui.search.SearchActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)

        setSupportActionBar(binding.toolbar.root)
        adapter = MoviesAdapter()
        binding.rvMovies.layoutManager = LinearLayoutManager(this)
        binding.rvMovies.adapter = adapter

        val factory = (application as MyApplication).appContainer.viewModelFactory
        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        binding.retryButton.setOnClickListener { viewModel.loadMovies() }
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
        binding.rvMovies.visibility = if (hasMovies) View.VISIBLE else View.GONE
        binding.stateContainer.visibility = if (!loading && !hasMovies) View.VISIBLE else View.GONE
        binding.retryButton.visibility = if (error) View.VISIBLE else View.GONE
        binding.tvStatus.setText(if (error) R.string.error_loading_movies else R.string.no_movies)
        adapter.submitList(state.movies)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) {
            startActivity(Intent(this, SearchActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
