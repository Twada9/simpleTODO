package com.example.simpletodo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simpletodo.Model.Todo
import com.example.simpletodo.ViewModel.LatestTodoListUiState
import com.example.simpletodo.ViewModel.MainViewModel
import com.example.simpletodo.ViewModel.Priority
import com.example.simpletodo.mock.FakeTodoDao
import com.example.simpletodo.ui.theme.SimpleTODOTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplicationPreview
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoCard(
    viewModel: MainViewModel = koinViewModel(),
    todo: Todo,
    modifier: Modifier = Modifier
) {
    var show by remember { mutableStateOf(true) }
    val transition = updateTransition(
        targetState = show,
        label = "visibilityTransition"
    )
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE)
    val formattedDate = todo.date?.let {
        Date(it)
    }?.let { dateFormat.format(it) } ?: ""

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
                        .clip(RoundedCornerShape(10.dp))
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
                    val priority = Priority.fromInt(todo.priority)
                    val gradient = Brush.horizontalGradient(
                        listOf(
                            Color(priority.gradationColor.start),
                            Color(priority.gradationColor.end)
                        )
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = gradient,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            // modifierで背景色が設定できないため
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                // ボーダーを表示するのに必要。.fillMaxHeight()が0になるため
                                .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                                    .background(Color(priority.borderColor))
                            )
                            Column(
                                modifier = Modifier.weight(1f) // 残りのスペースを使用
                            ) {
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
                                        .padding(4.dp)
                                )
                                Text(
                                    formattedDate,
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
fun TodoCardList(viewModel: MainViewModel = koinViewModel(), modifier: Modifier = Modifier) {
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
            val error = (uiState as LatestTodoListUiState.Error).exception
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("エラー: ${error.message}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoCardPreview() {
    // Create a sample Todo for the preview
    val sampleTodo = Todo(
        id = UUID.randomUUID(),
        title = "Sample Task",
        description = "This is a sample todo item for preview purposes. It shows how the card will look with some content.",
        date = Date().time, // Current date
        priority = 1 // Middle priority
    )

    KoinPreviewSetting {
        SimpleTODOTheme {
            Surface {
                TodoCard(
                    todo = sampleTodo
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoCardListPreview() {
    KoinPreviewSetting {
        SimpleTODOTheme {
            TodoCardList()
        }
    }
}

@Composable
fun KoinPreviewSetting(content: @Composable () -> Unit) {
    val previewModule = remember {
        module {
            viewModel { MainViewModel(FakeTodoDao()) }
        }
    }
    KoinApplicationPreview(
        application = { modules(previewModule) },
        content = content
    )
}