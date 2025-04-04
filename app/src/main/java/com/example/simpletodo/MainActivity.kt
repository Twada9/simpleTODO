package com.example.simpletodo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.simpletodo.Database.TodoDatabase
import com.example.simpletodo.Model.Todo
import com.example.simpletodo.ViewModel.LatestTodoListUiState
import com.example.simpletodo.ViewModel.MainViewModel
import com.example.simpletodo.ui.theme.SimpleTODOTheme
import kotlinx.coroutines.delay

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(viewModel: MainViewModel) {
    val isSheetOpen by viewModel.showModalView.collectAsState()
    val selectedTodo by viewModel.selectedTodo.collectAsState()
    val isNewTodo = selectedTodo.title.isEmpty() && selectedTodo.description.isEmpty()
    var title by remember(selectedTodo) { mutableStateOf(selectedTodo.title) }
    var description by remember(selectedTodo) { mutableStateOf(selectedTodo.description) }
    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.closeModalView()
            }
        ) {
            Column {
                TextField(
                    title,
                    onValueChange = { title = it },
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    label = { Text("title") },
                    maxLines = 1,
                )
                TextField(
                    description,
                    onValueChange = { description = it },
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    label = { Text("description") },
                    minLines = 5,
                )
                ExtendedFloatingActionButton(
                    onClick = {
                        if (isNewTodo) {
                            viewModel.add(title, description)
                        } else {
                            viewModel.update(selectedTodo, if (title == "") "title" else title , if (description == "") "description" else description)
                            viewModel.resetTodo()
                        }
                        viewModel.closeModalView()
                    },
                    Modifier
                        .padding(40.dp)
                        .fillMaxWidth(),
                ) {
                    Text(if (isNewTodo) "追加" else "更新")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoCard(viewModel: MainViewModel, todo: Todo, modifier: Modifier = Modifier) {
    var show by remember { mutableStateOf(true) }
    val transition = updateTransition(
        targetState = show,
        label = "visibilityTransition"
    )

    LaunchedEffect(transition.currentState, transition.targetState) {
        if (!transition.currentState && !transition.targetState) {
            // アニメーション終了後に削除処理
            delay(350)
            viewModel.delete(todo)
        }
    }

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { fullWidth -> fullWidth * 0.25f },
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    // 左スワイプ - 削除
                    show = false
                    true
                }
                else -> false
            }
        }
    )
    AnimatedVisibility(
        visible = show,
        ) {
        SwipeToDismissBox(
            dismissState,
            backgroundContent = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color = Color.Red)
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            },
            modifier = Modifier,
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = true,
            content = {
                Surface(onClick = {
                    viewModel.selectTodo(todo)
                    viewModel.openModalView()
                }) {
                    Card {
                        // 確認用
//                        Text(
//                            todo.id.toString(),
//                            modifier
//                                .fillMaxWidth()
//                                .padding(8.dp)
//                        )
                        Text(
                            todo.title,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        Text(
                            todo.description,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            },
        )
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
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is LatestTodoListUiState.Success -> {
            val todoList = (uiState as LatestTodoListUiState.Success).todo
            LazyColumn(
                modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(
                    todoList,
                    key = { it.id },
                ) { todo ->
                    TodoCard(viewModel, todo)
                }
            }
        }
        is LatestTodoListUiState.Error -> {

        }
    }
}

@Composable
fun AddButton(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = {
            viewModel.resetTodo()
            viewModel.openModalView()
    },
        modifier
            .wrapContentSize(Alignment.BottomEnd)
            .padding(),
        shape = CircleShape,
    ) {
        Icon(Icons.Filled.Add, "Large floating action button")
    }
}
