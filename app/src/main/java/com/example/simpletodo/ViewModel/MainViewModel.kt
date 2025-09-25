package com.example.simpletodo.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodo.Database.TodoDatabase
import com.example.simpletodo.Model.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class MainViewModel: ViewModel() {
    private lateinit var db: TodoDatabase
    private val _uiState = MutableStateFlow<LatestTodoListUiState>(LatestTodoListUiState.Success(emptyList()))
    val uiState: StateFlow<LatestTodoListUiState> = _uiState
    private val _showModalView = MutableStateFlow<Boolean>(false)
    val showModalView: StateFlow<Boolean> = _showModalView
    private val _selectedTodo = MutableStateFlow<Todo>(Todo(
        id = UUID.randomUUID(),
        title = "",
        description = "",
        date = Date().time,
        priority = 0
    ))
    val selectedTodo: StateFlow<Todo> = _selectedTodo

    private fun startObservingTodo() {
        viewModelScope.launch(Dispatchers.IO) {
            db.todoDao().getAll()
                .collect { todoList ->
                    _uiState.value = LatestTodoListUiState.Success(todoList)
                }
        }
    }

    fun initDatabase(context: Context) {
        // contextが必要なためactivityから呼び出す必要があるが、onCreateだと何度も呼ばれてしまうので
        // 以下の分岐を設ける
        if (!::db.isInitialized) {
            // TodoDatabaseクラスの初期化機能を使用
            db = TodoDatabase.initializeDatabase(context)
            startObservingTodo()
        }
    }

    fun add(title: String, description: String, date: Long, priority: Int) {
        val id = UUID.randomUUID()

        viewModelScope.launch(Dispatchers.IO) {
            db.todoDao().insert(Todo(id, title, description, date, priority))
        }
    }
    fun get() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.todoDao().getAll()
                    .collect { todoList ->
                        Log.d(null, todoList.first().title)
                        _uiState.value = LatestTodoListUiState.Success(todoList)
                    }
            } catch (e: Exception) {
                _uiState.value = LatestTodoListUiState.Error(e)
            }
        }
    }
    fun delete(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            db.todoDao().delete(todo)
        }
    }
    fun closeModalView() {
        _showModalView.value = false
    }
    fun openModalView() {
        _showModalView.value = true
    }
    fun selectTodo(todo: Todo) {
        _selectedTodo.value = todo
    }
    fun resetTodo() {
        _selectedTodo.value = Todo(
            UUID.randomUUID(),
            "",
            "",
            Date().time,
            Priority.MIDDLE.value
        )
    }
    fun update(todo: Todo, title: String, description: String, date: Long?, priority: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // 新しいTodoオブジェクトを作成（IDは同じままで内容を更新）
            val updatedTodo = todo.copy(title = title, description = description, date = date ?: Date().time, priority = priority)
            db.todoDao().update(updatedTodo)
        }
    }
}
sealed class LatestTodoListUiState {
    data class Success(val todo: List<Todo>): LatestTodoListUiState()
    data class Error(val exception: Throwable): LatestTodoListUiState()
}

enum class Priority(val value: Int, val displayName: String, val borderColor: Long, val gradationColor: Gradation) {
    HIGH(0, "高", 0xFFFF4757, Gradation(0x14FF4757, 0x05FF4757)),
    MIDDLE(1, "中", 0xFFFFA502, Gradation(0x14FFA502, 0x05FFA502)),
    LOW(2, "低", 0xFF2ED573, Gradation(0x142ED573, 0x052ED573));

    companion object {
        fun fromInt(value: Int): Priority {
            return entries.find { it.value == value } ?: MIDDLE
        }
    }
}

class Gradation(
    val start: Long,
    val end: Long
)