package com.example.simpletodo

import com.example.simpletodo.ui.components.AddButton
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.simpletodo.ViewModel.MainViewModel
import com.example.simpletodo.ui.components.BottomSheet
import com.example.simpletodo.ui.components.TodoCardList
import com.example.simpletodo.ui.theme.SimpleTODOTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initDatabase(context = applicationContext)

        enableEdgeToEdge()
        setContent {
            SimpleTODOTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        AddButton(viewModel)
                }) { innerPadding ->
                    TodoCardList(viewModel, Modifier.padding(innerPadding))
                    BottomSheet(viewModel)
                }
            }
        }
    }
}
