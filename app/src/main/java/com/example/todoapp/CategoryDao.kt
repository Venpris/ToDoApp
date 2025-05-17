package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAll(): LiveData<List<Category>>

    @Insert
    fun insertAll(vararg categories: Category)

    @Query("SELECT id FROM categories ORDER BY id DESC LIMIT 1")
    fun getLastInsertedCategory(): Int

    @Delete
    fun deleteCategories(vararg categories: Category)

    @Update
    fun updateCategories(vararg categories: Category)
}