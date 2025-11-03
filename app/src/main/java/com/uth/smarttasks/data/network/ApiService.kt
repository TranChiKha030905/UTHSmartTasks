package com.uth.smarttasks.data.network

import com.uth.smarttasks.data.model.ApiResponse
import com.uth.smarttasks.data.model.Task
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // GET /tasks trả về một *danh sách* Task bọc trong ApiResponse
    @GET("tasks")
    suspend fun getTasks(): ApiResponse<List<Task>>

    // GET /task/{id} trả về một *Task đơn* bọc trong ApiResponse
    @GET("task/{id}")
    suspend fun getTaskDetail(@Path("id") id: String): ApiResponse<Task>

    // DELETE /task/{id}
    @DELETE("task/{id}")
    suspend fun deleteTask(@Path("id") id: String): ApiResponse<Unit>

    // === CODE MỚI THÊM VÀO ===

    // POST /tasks (Giả định: gửi 1 object Task, nhận về 1 Task đã tạo)
    // Mày có thể cần 1 data class riêng cho Body (ví dụ: CreateTaskRequest)
    // Nhưng để đơn giản, tao dùng 1 object Map
    @POST("tasks")
    suspend fun createTask(@Body taskData: Map<String, String>): ApiResponse<Task>

    // PUT /task/{id} (Giả định: gửi 1 object Task, nhận về Task đã update)
    // Giả định là ta gửi *toàn bộ* object Task lên
    @PUT("task/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body task: Task
    ): ApiResponse<Task>
}