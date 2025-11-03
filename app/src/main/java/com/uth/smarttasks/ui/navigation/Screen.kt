package com.uth.smarttasks.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

// Định nghĩa TẤT CẢ các đường dẫn - "route"
sealed class Screen(val route: String) {
    // Màn hình độc lập
    object Welcome : Screen("welcome")
    object Login : Screen("login")

    // 3 Màn hình trong Bottom Nav
    object Home : Screen("home") // <-- Màn hình 4 nút
    object TaskList : Screen("task_list") // <-- Màn hình danh sách task
    object Profile : Screen("profile")

    // Màn hình chi tiết (không có Bottom Nav)
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }

    // Màn hình tạo task (Không có Bottom Nav)
    object CreateTask : Screen("create_task")

    // 3 Màn hình "ra ngô ra khoai"
    object Projects : Screen("projects")
    object Calendar : Screen("calendar")
    object Statistics : Screen("statistics")
}

// Data class cho các item trên Bottom Nav
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// --- ĐÂY LÀ CHỖ SỬA ---
// List các item cho Bottom Nav (Đã sửa lại cho đúng)
val bottomNavItems = listOf(
    // Tab 1: Home
    BottomNavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = Screen.Home.route // <-- Phải trỏ về Home (4 nút)
    ),
    // Tab 2: Tasks
    BottomNavItem(
        label = "Tasks",
        icon = Icons.Default.List,
        route = Screen.TaskList.route // <-- Phải trỏ về TaskList
    ),
    // Tab 3: Profile
    BottomNavItem(
        label = "Profile",
        icon = Icons.Default.AccountCircle,
        route = Screen.Profile.route
    )
)