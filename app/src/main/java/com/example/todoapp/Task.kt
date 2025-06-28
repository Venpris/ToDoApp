package com.example.todoapp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "tasks", foreignKeys = [
    ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.SET_NULL
    )
])
data class Task (
    @PrimaryKey(autoGenerate = true) var id: Int,
    var title: String,
    var description: String?,
    var dateEpochDay: Long,
    var timeNanoOfDay: Long?, // null if all-day
    val categoryId: Int?,
    val isStarred: Boolean = false
)