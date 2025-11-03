package com.uth.smarttasks.data.model

// Model chính cho Task (Dựa trên api.txt)
data class Task(
    val id: String, // Trong api.txt là Int (1), nhưng để String cho an toàn khi làm route
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val category: String,
    val dueDate: String,
    val subtasks: List<Subtask>,
    val attachments: List<Attachment>
)

// Model cho Subtask
data class Subtask(
    val id: Int,
    val title: String,
    val isCompleted: Boolean
)

// Model cho Attachment
data class Attachment(
    val id: Int,
    val fileName: String,
    val fileUrl: String
)