package com.example.simpletodo.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simpletodo.DataAccess.TodoDao
import com.example.simpletodo.Model.Todo

@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}