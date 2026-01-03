package com.example.simpletodo.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodo.DataAccess.TodoDao
import com.example.simpletodo.Model.Todo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class MainViewModel(
    private val dao: TodoDao,
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5000)
) : ViewModel() {
    val uiState: StateFlow<LatestTodoListUiState> = dao.getAll()
    .map<List<Todo>, LatestTodoListUiState> { todoList ->
        LatestTodoListUiState.Success(todoList)
    }
    .catch { e ->
        emit(LatestTodoListUiState.Error(e))
    }
    .stateIn(
        viewModelScope,
        sharingStarted,
        LatestTodoListUiState.Loading
    )
    private val _showModalView = MutableStateFlow<Boolean>(false)
    val showModalView: StateFlow<Boolean> = _showModalView
    private val _selectedTodo = MutableStateFlow<Todo>(
        Todo(
            id = UUID.randomUUID(),
            title = "",
            description = "",
            date = Date().time,
            priority = 0
        )
    )
    val selectedTodo: StateFlow<Todo> = _selectedTodo

    fun add(title: String, description: String, date: Long, priority: Int) {
        val id = UUID.randomUUID()

        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(Todo(id, title, description, date, priority))
        }
    }

    fun delete(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(todo)
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
            val updatedTodo = todo.copy(
                title = title,
                description = description,
                date = date ?: Date().time,
                priority = priority
            )
            dao.update(updatedTodo)
        }
    }
}

sealed class LatestTodoListUiState {
    data class Success(val todo: List<Todo>) : LatestTodoListUiState()
    data class Error(val exception: Throwable) : LatestTodoListUiState()
    data object Loading : LatestTodoListUiState()
}

enum class Priority(
    val value: Int,
    val displayName: String,
    val borderColor: Long,
    val gradationColor: Gradation
) {
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