package com.example.todoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isDone = 0")
    fun getAll(): List<Task>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId AND isDone = 0")
    fun filterTasksByCategory(categoryId: Int): List<Task>

    @Query("SELECT * FROM tasks WHERE isDone = 1")
    fun getDoneTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE isStarred = 1 AND isDone = 0")
    fun getStarredTasks(): List<Task>

    @Insert
    fun insertAll(vararg tasks: Task)

    @Delete
    fun deleteTasks(vararg tasks: Task)

    @Update
    fun updateTasks(vararg tasks: Task)
}