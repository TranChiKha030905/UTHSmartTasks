package com.uth.smarttasks.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

// 1. Thêm @Entity
@Entity(tableName = "tasks")
// 3. Thêm @TypeConverters để Room biết lưu List
@TypeConverters(DatabaseConverters::class)
data class Task(
    // 2. Thêm @PrimaryKey
    @PrimaryKey
    val id: String, // Dùng ID từ API hoặc ID tự tạo
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val category: String,
    val dueDate: String,
    val subtasks: List<Subtask>,
    val attachments: List<Attachment>
)

data class Subtask(
    val id: Int,
    val title: String,
    val isCompleted: Boolean
)

data class Attachment(
    val id: Int,
    val fileName: String,
    val fileUrl: String
)