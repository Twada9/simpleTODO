package com.example.simpletodo.DataAccess

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.simpletodo.Model.Todo

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): List<Todo>

    @Delete
    fun delete(todo: Todo)

    @Insert
    fun insert(todo: Todo)
}