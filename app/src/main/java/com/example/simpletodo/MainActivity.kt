package com.example.simpletodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.simpletodo.ViewModel.MainViewModel
import com.example.simpletodo.ui.screen.TodoListScreen

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initDatabase(context = applicationContext)

        enableEdgeToEdge()
        setContent {
            TodoListScreen(viewModel)
        }
    }
}
