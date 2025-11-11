package com.uth.smarttasks

import android.app.Application
import com.uth.smarttasks.data.local.TaskDatabase
import com.uth.smarttasks.data.local.ThemeDataStore // <-- THÊM IMPORT
import com.uth.smarttasks.data.network.RetrofitClient
import com.uth.smarttasks.data.repository.TaskRepository

class SmartTasksApplication : Application() {

    // Database (Room)
    private val database by lazy { TaskDatabase.getDatabase(this) }

    // Repository (cho Task)
    val taskRepository by lazy {
        TaskRepository(database.taskDao(), RetrofitClient.apiService)
    }
    val themeDataStore by lazy { ThemeDataStore(this) }
}