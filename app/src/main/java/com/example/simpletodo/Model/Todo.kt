package com.example.simpletodo.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Todo(
    @PrimaryKey val id: UUID,
    @ColumnInfo val title: String,
    @ColumnInfo val description: String,
    @ColumnInfo val date: Long?,
    @ColumnInfo val priority: Int,
)