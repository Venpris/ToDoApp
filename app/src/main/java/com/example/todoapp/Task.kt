package com.example.todoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val description: String?,
    val dateEpochDay: Long,
    val timeNanoOfDay: Long?, // null if all-day
    val categoryId: Int?,
    val isDone: Boolean = false,
    val isSelected: Boolean = false,
    val isStarred: Boolean = false
)
