package com.example.simpletodo.ViewModel

import android.content.ClipDescription
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.simpletodo.Database.TodoDatabase
import com.example.simpletodo.Model.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel: ViewModel() {
    private lateinit var db: TodoDatabase
    private val _uiState = MutableStateFlow<LatestTodoListUiState>(LatestTodoListUiState.Success(emptyList()))
    val uiState: StateFlow<LatestTodoListUiState> = _uiState
    private val _showModalView = MutableStateFlow<Boolean>(false)
    val showModalView: StateFlow<Boolean> = _showModalView

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
            db = Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                "todo_database"
            ).build()
            startObservingTodo()
        }
    }

    fun add(title: String, description: String) {
        val id = UUID.randomUUID()

        viewModelScope.launch(Dispatchers.IO) {
            db.todoDao().insert(Todo(id, title, description))
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
}
sealed class LatestTodoListUiState {
    data class Success(val todo: List<Todo>): LatestTodoListUiState()
    data class Error(val exception: Throwable): LatestTodoListUiState()
}