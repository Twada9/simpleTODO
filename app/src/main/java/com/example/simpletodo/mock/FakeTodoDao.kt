package com.example.simpletodo.mock

import com.example.simpletodo.DataAccess.TodoDao
import com.example.simpletodo.Model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class FakeTodoDao : TodoDao {
    private val todos = MutableStateFlow<List<Todo>>(
        listOf(
            Todo(
                id = UUID.randomUUID(),
                title = "買い物",
                description = "牛乳とパンを買う",
                date = System.currentTimeMillis(),
                priority = 1
            ),
            Todo(
                id = UUID.randomUUID(),
                title = "勉強",
                description = "Kotlinの勉強をする",
                date = System.currentTimeMillis() + 86400000,
                priority = 2
            ),
            Todo(
                id = UUID.randomUUID(),
                title = "運動",
                description = "ジョギング30分",
                date = null,
                priority = 0
            )
        )
    )

    override fun insert(todo: Todo) {
        todos.value = todos.value + todo
    }

    override fun update(todo: Todo) {
        todos.value = todos.value.map { if (it.id == todo.id) todo else it }
    }

    override fun delete(todo: Todo) {
        todos.value = todos.value.filter { it.id != todo.id }
    }

    override fun getAll(): Flow<List<Todo>> = todos
}
