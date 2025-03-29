package com.example.todoapp

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAll(): List<Task>
}