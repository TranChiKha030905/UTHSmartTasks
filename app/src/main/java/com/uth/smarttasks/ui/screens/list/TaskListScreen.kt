package com.uth.smarttasks.ui.screens.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uth.smarttasks.R // Import R để lấy ảnh
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.ui.navigation.Screen
import com.uth.smarttasks.ui.viewmodel.TaskListViewModel

// --- HÀM CHÍNH ĐÃ SỬA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskListViewModel = viewModel()
) {
    val uiState = viewModel.uiState.value

    Scaffold(
        // Thêm Floating Action Button (FAB)
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Mở màn hình CreateTask
                    navController.navigate(Screen.CreateTask.route)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Padding từ Scaffold
        ) {
            when {
                // 1. Loading
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // 2. Error
                uiState.error != null -> {
                    Text(
                        text = "Lỗi: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                // 3. Empty List
                uiState.tasks.isEmpty() -> {
                    EmptyListView(modifier = Modifier.align(Alignment.Center))
                }
                // 4. Success (List có data)
                else -> {
                    TaskList(
                        tasks = uiState.tasks,
                        navController = navController,
                        viewModel = viewModel // Truyền ViewModel xuống
                    )
                }
            }
        }
    }
}

// Composable cho danh sách (Thêm param viewModel)
@Composable
fun TaskList(
    tasks: List<Task>,
    navController: NavController,
    viewModel: TaskListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onItemClick = {
                    // Click vào item -> Mở chi tiết
                    navController.navigate(Screen.TaskDetail.createRoute(task.id))
                },
                onCheckedChange = {
                    // Click vào checkbox -> Gọi ViewModel
                    viewModel.toggleTaskStatus(task)
                }
            )
        }
    }
}

// --- Composable cho 1 item (ĐÃ SỬA LẠI HOÀN TOÀN) ---
@Composable
fun TaskItem(
    task: Task,
    onItemClick: () -> Unit,
    onCheckedChange: () -> Unit
) {
    val isCompleted = task.status.lowercase() == "completed"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() } // Click cả row
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onCheckedChange() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 2. Cột Tiêu đề và Ngày
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    // Nếu hoàn thành thì gạch ngang
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Due: ${task.dueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. Status Tag (Như trong ảnh)
            Text(
                text = task.status,
                style = MaterialTheme.typography.labelSmall,
                color = when (task.status.lowercase()) {
                    "in progress" -> Color(0xFFFFA500) // Cam
                    "pending" -> Color.Red
                    "completed" -> Color.Green
                    else -> Color.Gray
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

// Composable cho màn hình trống (Giữ nguyên)
@Composable
fun EmptyListView(modifier: Modifier = Modifier) {
    // ... (Code y hệt cũ) ...
}