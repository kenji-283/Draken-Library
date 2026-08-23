package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.navigation.DrakensNavGraph
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DrakensLibraryTheme
import com.example.ui.viewmodel.LibraryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrakensLibraryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CharcoalBlack
                ) {
                    DrakensNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}

