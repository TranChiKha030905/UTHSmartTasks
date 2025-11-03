package com.uth.smarttasks.data.model

data class ApiResponse<T>(
    val isSuccess: Boolean,
    val message: String,
    val data: T
)