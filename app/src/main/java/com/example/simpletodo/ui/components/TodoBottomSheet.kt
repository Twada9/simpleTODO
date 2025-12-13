package com.example.simpletodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.simpletodo.ViewModel.MainViewModel
import com.example.simpletodo.ViewModel.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var datePickerState by remember {
        mutableStateOf(DatePickerState(
                locale = Locale.JAPANESE,
            initialSelectedDateMillis = selectedTodo.date ?: Date().time
        ))
    }
    var previousDateState by remember { mutableStateOf(0.toLong()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE)
    val formattedDate =
        dateFormat.format(datePickerState.selectedDateMillis?.let { Date(it) } ?: Date())
    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
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
                                previousDateState =
                                    datePickerState.selectedDateMillis ?: Date().time
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
                            viewModel.add(
                                title,
                                description,
                                datePickerState.selectedDateMillis ?: Date().time,
                                selectedPriority.value
                            )
                        } else {
                            viewModel.update(
                                selectedTodo,
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
