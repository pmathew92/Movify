package com.movify.app.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.movify.app.MyApplication
import com.movify.app.ui.compose.main.MainRoute
import com.movify.app.ui.compose.theme.MovifyTheme
import com.movify.app.ui.main.MainViewModel

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val factory = (application as MyApplication).appContainer.viewModelFactory
        setContent {
            MovifyTheme {
                val mainViewModel: MainViewModel = viewModel(factory = factory)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainRoute(
                        viewModel = mainViewModel,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
