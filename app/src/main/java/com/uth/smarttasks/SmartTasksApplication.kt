package com.uth.smarttasks

import android.app.Application
import com.uth.smarttasks.data.local.TaskDatabase
import com.uth.smarttasks.data.network.RetrofitClient
import com.uth.smarttasks.data.repository.TaskRepository

// Lớp này chạy đầu tiên khi app mở
class SmartTasksApplication : Application() {

    // Khởi tạo Database
    private val database by lazy { TaskDatabase.getDatabase(this) }

    // Khởi tạo "Bộ não" Repository
    // Nó cần cả Room (database.taskDao()) và Retrofit (RetrofitClient.apiService)
    val taskRepository by lazy {
        TaskRepository(database.taskDao(), RetrofitClient.apiService)
    }
}