package com.uth.smarttasks.ui.screens.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uth.smarttasks.R
import com.uth.smarttasks.SmartTasksApplication
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.ui.navigation.Screen
import com.uth.smarttasks.ui.viewmodel.TaskListViewModel
import com.uth.smarttasks.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
) {
    // Sửa cách gọi ViewModel
    val application = LocalContext.current.applicationContext as SmartTasksApplication
    val viewModel: TaskListViewModel = viewModel(
        factory = ViewModelFactory(application.taskRepository)
    )

    // Sửa lỗi 1: Dùng collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        // Thêm TopAppBar với nút Refresh 🔄
        topBar = {
            TopAppBar(
                title = { Text("My Tasks", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.refreshTasks() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh (Đồng bộ)")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateTask.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sửa lỗi 2: Dùng logic mới (không có 'error')
            when {
                // Đang tải (lần đầu)
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // Tải xong nhưng list rỗng
                uiState.tasks.isEmpty() && !uiState.isLoading -> {
                    EmptyListView(modifier = Modifier.align(Alignment.Center))
                }
                // Tải xong và có data
                else -> {
                    TaskList(
                        tasks = uiState.tasks,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

// Composable cho danh sách
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
                    navController.navigate(Screen.TaskDetail.createRoute(task.id))
                },
                onCheckedChange = {
                    viewModel.toggleTaskStatus(task)
                }
            )
        }
    }
}

// Composable cho 1 item (PUBLIC - KHÔNG CÓ 'private')
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
                .clickable { onItemClick() }
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onCheckedChange() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
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

            Text(
                text = task.status,
                style = MaterialTheme.typography.labelSmall,
                color = when (task.status.lowercase()) {
                    "in progress" -> Color(0xFFFFA500)
                    "pending" -> Color.Red
                    "completed" -> Color.Green
                    else -> Color.Gray
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

// Composable cho màn hình trống
@Composable
fun EmptyListView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_list_empty),
            contentDescription = "Empty List",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stay productive!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your task list is empty. \nTime to add some new goals.",
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}