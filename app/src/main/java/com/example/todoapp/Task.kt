package com.example.todoapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "tasks")
data class Task (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val description: String?,
    val date: LocalDate,
    val time: LocalTime?,
    val isDone: Boolean,
    val isStarred: Boolean,
    val subTasks: List<Task>?
)
