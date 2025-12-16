package com.example.simpletodo.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.simpletodo.ViewModel.MainViewModel
import com.example.simpletodo.ui.components.AddButton
import com.example.simpletodo.ui.components.BottomSheet
import com.example.simpletodo.ui.components.TodoCardList
import com.example.simpletodo.ui.theme.SimpleTODOTheme

@Composable
fun TodoListScreen(
    viewModel: MainViewModel
) {
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