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

class MainViewModel: ViewModel() {
    fun createDB(context: Context) {
        val db = Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo"
        ).build()
        viewModelScope.launch(Dispatchers.IO) {
//            db.todoDao().insert(Todo(1, "title", "description"))
            val todo = db.todoDao().getAll()
            print(todo.first().title)
            Log.d(null, todo.first().title)
        }
    }
}