package com.uth.smarttasks.data.local

import androidx.room.*
import com.uth.smarttasks.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Lấy TẤT CẢ task và TỰ ĐỘNG cập nhật khi có thay đổi (dùng Flow)
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): Task?

    // Thêm (hoặc Thay thế) một list task (khi đồng bộ)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    // Thêm (hoặc Thay thế) 1 task (khi tạo mới)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("DELETE FROM tasks")
    suspend fun clearAll()
}