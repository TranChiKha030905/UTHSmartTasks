package com.uth.smarttasks.data.repository

import com.uth.smarttasks.data.local.TaskDao
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.network.ApiService
import kotlinx.coroutines.flow.Flow

// Đây là "Bộ não"
class TaskRepository(
    private val taskDao: TaskDao,
    private val apiService: ApiService
) {

    // 1. LẤY DATA (Từ Room)
    // App sẽ luôn đọc từ đây, nó tự động cập nhật
    fun getTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    // 2. ĐỒNG BỘ (Từ API -> Room)
    // Đây là hàm Refresh 🔄
    suspend fun refreshTasks() {
        try {
            // Lấy data từ API
            val apiResponse = apiService.getTasks()
            if (apiResponse.isSuccess) {
                // Xóa data cũ trong Room
                taskDao.clearAll()
                // Lưu data mới vào Room
                taskDao.insertAll(apiResponse.data)
            }
        } catch (e: Exception) {
            // Xử lý lỗi mạng (nếu cần)
            e.printStackTrace()
        }
    }

    // 3. XÀI THỎA THÍCH (Chỉ đụng vào Room)

    suspend fun getTaskById(id: String): Task? {
        return taskDao.getTaskById(id)
    }

    suspend fun addTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(id: String) {
        taskDao.deleteTaskById(id)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
}