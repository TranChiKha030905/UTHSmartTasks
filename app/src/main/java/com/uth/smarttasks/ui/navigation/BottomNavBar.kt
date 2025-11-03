package com.uth.smarttasks.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Lấy danh sách item từ file Screen.kt
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,

                // --- ĐÂY LÀ LOGIC SỬA LỖI "GIẬT" ---
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph
                        // (Tìm màn hình gốc, thường là 'home')
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Đảm bảo không tạo 2 bản copy của cùng 1 màn hình
                        launchSingleTop = true
                        // Phục hồi state khi nhấn lại
                        restoreState = true
                    }
                }
            )
        }
    }
}