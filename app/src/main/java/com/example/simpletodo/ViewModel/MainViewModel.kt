package com.example.simpletodo.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.simpletodo.Database.TodoDatabase
import com.example.simpletodo.Model.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel: ViewModel() {
    private lateinit var db: TodoDatabase
    
    fun initDatabase(context: Context) {
        // contextが必要なためactivityから呼び出す必要があるが、onCreateだと何度も呼ばれてしまうので
        // 以下の分岐を設ける
        if (!::db.isInitialized) {
            db = Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                "todo_database"
            ).build()
        }
    }

    fun add() {
        val id = UUID.randomUUID()

        viewModelScope.launch(Dispatchers.IO) {
            db.todoDao().insert(Todo(id, "title", "description"))
        }
        viewModelScope.launch(Dispatchers.IO) {
            val todo = db.todoDao().getAll()
            print(todo.first().title)
            Log.d(null, todo.first().title)
        }
    }
    fun get() {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = db.todoDao().getAll()
            print(todo.first().title)
            Log.d(null, todo.first().title)
        }
    }
}