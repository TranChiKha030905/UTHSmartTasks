package com.uth.smarttasks.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

// Định nghĩa các đường dẫn - "route"
sealed class Screen(val route: String) {
    // Màn hình độc lập
    object Welcome : Screen("welcome")
    object Login : Screen("login")

    // 3 Màn hình trong Bottom Nav
    object Home : Screen("home")
    object TaskList : Screen("task_list")
    object Profile : Screen("profile")

    // Màn hình chi tiết (không có Bottom Nav)
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }

    // Màn hình tạo task (Không có Bottom Nav)
    object CreateTask : Screen("create_task")

    // --- 3 ROUTE MỚI CHO 3 NÚT ---
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

// List các item cho Bottom Nav (Giữ nguyên)
val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
    BottomNavItem("Tasks", Icons.Default.List, Screen.TaskList.route),
    BottomNavItem("Profile", Icons.Default.AccountCircle, Screen.Profile.route)
)