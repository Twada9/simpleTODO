package com.example.simpletodo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
                    TodoCardList(viewModel)
                }
            }
        }
    }
}

@Composable
fun TodoCard(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Surface(onClick = {
//        viewModel.add()
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
    val viewModel = MainViewModel()
    TodoCardList(viewModel)
}

@Composable
fun TodoCardList(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
        items(10) {
            TodoCard(viewModel)
        }
    }
}

@Composable
fun AddButton(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = {
        viewModel.add()
    },
        modifier
            .wrapContentSize(Alignment.BottomEnd)
            .padding(),
        shape = CircleShape,
    ) {
        Icon(Icons.Filled.Add, "Large floating action button")
    }
}
