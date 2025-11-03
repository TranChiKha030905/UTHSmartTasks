package com.uth.smarttasks.ui.screens.detail

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.ui.viewmodel.TaskDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavController,
    taskId: String,
    // Khởi tạo ViewModel
    viewModel: TaskDetailViewModel = viewModel()
) {
    val uiState = viewModel.uiState.value
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Gọi API load chi tiết *một lần* khi Composable được khởi tạo
    LaunchedEffect(key1 = taskId) {
        viewModel.loadTaskDetail(taskId)
    }

    // Theo dõi state `deletionSuccess`
    LaunchedEffect(key1 = uiState.deletionSuccess) {
        if (uiState.deletionSuccess) {
            Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
            navController.popBackStack() // Quay lại màn hình list
        }
    }

    // Theo dõi lỗi
    LaunchedEffect(key1 = uiState.error) {
        val error = uiState.error
        if (error != null) {
            // Chỉ hiển thị Toast lỗi, không thoát màn hình
            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                // Nút Back
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                // Nút Xóa
                actions = {
                    // Chỉ hiển thị nút xóa nếu load task thành công
                    if (uiState.task != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // 1. Loading
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // 2. Error (Lỗi nghiêm trọng, không load được)
                uiState.error != null && uiState.task == null -> {
                    Text(
                        text = "Lỗi: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                // 3. Success
                uiState.task != null -> {
                    TaskDetailContent(task = uiState.task)
                }
            }
        }
    }

    // Dialog xác nhận xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTask(taskId)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Composable cho nội dung chi tiết
@Composable
fun TaskDetailContent(task: Task) {
    // Dùng LazyColumn để cuộn được
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Tiêu đề và Mô tả
        item {
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge
            )
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }

        // 2. Thông tin (Category, Status, Priority)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoChip(label = "Category", value = task.category)
                InfoChip(label = "Status", value = task.status)
                InfoChip(label = "Priority", value = task.priority)
            }
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }

        // 3. Subtasks
        item {
            Text(
                text = "Subtasks (${task.subtasks.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(task.subtasks) { subtask ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                Checkbox(
                    checked = subtask.isCompleted,
                    onCheckedChange = null, // Disable
                    enabled = false
                )
                Text(text = subtask.title) // Dùng title từ api.txt
            }
        }

        // 4. Attachments
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Attachments (${task.attachments.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(task.attachments) { attachment ->
            Text(
                text = attachment.fileName, // Dùng fileName từ api.txt
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }
    }
}

// Composable cho các chip thông tin
@Composable
fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}