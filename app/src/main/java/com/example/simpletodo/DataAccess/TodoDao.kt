package com.example.simpletodo.DataAccess

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.simpletodo.Model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): Flow<List<Todo>>

    @Delete
    fun delete(todo: Todo)

    @Insert
    fun insert(todo: Todo)

    @Update
    fun update(todo: Todo)
}