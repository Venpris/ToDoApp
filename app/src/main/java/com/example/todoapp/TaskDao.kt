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
    @Query("SELECT * FROM tasks " +
            "ORDER BY dateEpochDay ASC, " +
            "timeNanoOfDay is NULL, " +
            "timeNanoOfDay ASC")
    fun getAll(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId " +
            "ORDER BY dateEpochDay ASC, " +
            "timeNanoOfDay is NULL, " +
            "timeNanoOfDay ASC")
    fun filterTasksByCategory(categoryId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isStarred = 1 " +
            "ORDER BY dateEpochDay ASC, " +
            "timeNanoOfDay is NULL, " +
            "timeNanoOfDay ASC")
    fun getStarredTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    fun getSubtasksForTask(taskId: Int): LiveData<List<Subtask>>

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    fun getSubtasksForTaskSync(taskId: Int): List<Subtask>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): LiveData<Task>

    @Insert
    fun insertAll(vararg tasks: Task): List<Long>

    @Insert
    fun insertSubtasks(vararg subtasks: Subtask): List<Long>

    @Insert
    fun insertSubtask(subtask: Subtask): Long

    @Delete
    fun deleteTasks(vararg tasks: Task)

    @Delete
    fun deleteSubtask(subtask: Subtask)

    @Update
    fun updateTasks(vararg tasks: Task)

    @Update
    fun updateSubtask(subtask: Subtask)

    @Transaction
    @Query("SELECT * FROM tasks")
    fun getAllWithSubtasks(): LiveData<List<TaskWithSubtasks>>
}