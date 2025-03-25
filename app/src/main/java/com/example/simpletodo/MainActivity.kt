package com.example.simpletodo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.simpletodo.Database.TodoDatabase
import com.example.simpletodo.Model.Todo
import com.example.simpletodo.ViewModel.MainViewModel
import com.example.simpletodo.ui.theme.SimpleTODOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleTODOTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoCardList()
                }
            }
        }
    }
}

@Composable
fun TodoCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Surface(onClick = {
        val viewModel = MainViewModel()
        viewModel.createDB(context)
    }) {
        Card {
            Text(
                "title",
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)

            )
        }
    }
}

@Preview
@Composable
fun TodoCardPreview() {
    TodoCardList()
}

@Composable
fun TodoCardList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
        items(10) {
            TodoCard()
        }
    }
}