package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isDone = 0")
    fun getAll(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId AND isDone = 0")
    fun filterTasksByCategory(categoryId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isDone = 1")
    fun getDoneTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isStarred = 1 AND isDone = 0")
    fun getStarredTasks(): LiveData<List<Task>>

    @Insert
    fun insertAll(vararg tasks: Task)

    @Insert
    fun insertSubtasks(vararg subtasks: Subtask)

    @Delete
    fun deleteTasks(vararg tasks: Task)

    @Update
    fun updateTasks(vararg tasks: Task)

    @Transaction
    @Query("SELECT * FROM tasks")
    fun getAllWithSubtasks(): LiveData<List<TaskWithSubtasks>>
}