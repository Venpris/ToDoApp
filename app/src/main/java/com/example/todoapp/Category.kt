package com.example.todoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    var tasks: List<Task>?,
    val isStarredCategory: Boolean = false
)