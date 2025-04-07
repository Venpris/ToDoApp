package com.example.todoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAll(): List<Category>

    @Insert
    fun insertAll(vararg categories: Category)

    @Delete
    fun deleteCategories(vararg categories: Category)

    @Update
    fun updateCategories(vararg categories: Category)
}