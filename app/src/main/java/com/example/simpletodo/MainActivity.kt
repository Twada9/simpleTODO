package com.example.simpletodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simpletodo.Model.Todo
import com.example.simpletodo.ViewModel.LatestTodoListUiState
import com.example.simpletodo.ViewModel.MainViewModel
import com.example.simpletodo.ViewModel.Priority
import com.example.simpletodo.ui.theme.SimpleTODOTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

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
    var selectedPriority by remember(selectedTodo) { mutableStateOf(Priority.fromInt(selectedTodo.priority)) }
    var priorityDropdownExpanded by remember { mutableStateOf(false) }
    var datePickerState by remember { mutableStateOf(DatePickerState(locale = Locale.JAPANESE)) }
    var previousDateState by remember { mutableStateOf(0.toLong()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE)
    val formattedDate = dateFormat.format(datePickerState.selectedDateMillis?.let { Date(it) } ?: Date())

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TextField(
                        value = formattedDate.toString(),
                        onValueChange = { },
                        label = { Text("expiration") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                showDatePicker = !showDatePicker
                                previousDateState = datePickerState.selectedDateMillis ?: Date().time
                            }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select date"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                    

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = {
                                showDatePicker = false
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDatePicker = false
                                    },
                                    enabled = true
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showDatePicker = false
                                        datePickerState.selectedDateMillis = previousDateState
                                    },
                                ) {
                                    Text("キャンセル")
                                }
                            }
                        ) {
                            DatePicker(
                                state = datePickerState,
                                showModeToggle = showDatePicker
                            )
                        }
                    }
                }
                
                // 優先度選択
                ExposedDropdownMenuBox(
                    expanded = priorityDropdownExpanded,
                    onExpandedChange = { priorityDropdownExpanded = !priorityDropdownExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TextField(
                        value = selectedPriority.displayName,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("優先度") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = priorityDropdownExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = priorityDropdownExpanded,
                        onDismissRequest = { priorityDropdownExpanded = false }
                    ) {
                        Priority.entries.forEach { priority ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .width(20.dp)
                                                .height(20.dp)
                                                .background(
                                                    Color(priority.borderColor),
                                                    RoundedCornerShape(4.dp)
                                                )
                                        )
                                        Text(
                                            text = priority.displayName,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                },
                                onClick = {
                                    selectedPriority = priority
                                    priorityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                
                ExtendedFloatingActionButton(
                    onClick = {
                        if (isNewTodo) {
                            viewModel.add(title, description, datePickerState.selectedDateMillis ?: Date().time, selectedPriority.value)
                        } else {
                            viewModel.update(selectedTodo,
                                if (title == "") "title" else title,
                                if (description == "") "description" else description,
                                datePickerState.selectedDateMillis,
                                selectedPriority.value
                            )
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
                    val gradient = Brush.horizontalGradient(listOf(Color(priority.gradationColor.start), Color(priority.gradationColor.end)))
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
                                // ボーダーが表示するのに必要.fillMaxHeight()が0になるため
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
@Preview
@Composable
fun TodoCardListPreview() {
    val viewModel = MainViewModel()
    TodoCardList(viewModel)
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
    
    // Create a mock viewModel (for preview purposes only)
    val mockViewModel = MainViewModel()
    
    SimpleTODOTheme {
        Surface {
            TodoCard(
                viewModel = mockViewModel,
                todo = sampleTodo
            )
        }
    }
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
