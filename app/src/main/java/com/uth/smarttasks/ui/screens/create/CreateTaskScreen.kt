package com.uth.smarttasks.ui.screens.create

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uth.smarttasks.ui.viewmodel.CreateTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    navController: NavController,
    viewModel: CreateTaskViewModel = viewModel()
) {
    val uiState = viewModel.uiState.value
    val context = LocalContext.current

    // State cho các TextField
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    // Xử lý khi tạo task thành công
    LaunchedEffect(key1 = uiState.creationSuccess) {
        if (uiState.creationSuccess) {
            Toast.makeText(context, "Task created!", Toast.LENGTH_SHORT).show()
            navController.popBackStack() // Quay lại màn hình list
        }
    }

    // Xử lý khi có lỗi
    LaunchedEffect(key1 = uiState.error) {
        if(uiState.error != null) {
            Toast.makeText(context, "Error: ${uiState.error}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Task", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Cho phép cuộn
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            // Category
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (e.g., Work, Personal)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Priority
            OutlinedTextField(
                value = priority,
                onValueChange = { priority = it },
                label = { Text("Priority (e.g., High, Medium, Low)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Due Date
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Due Date (e.g., 2025-11-05)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nút Create
            Button(
                onClick = {
                    // TODO: Thêm validation (kiểm tra rỗng)
                    viewModel.createTask(title, description, category, priority, dueDate)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading // Vô hiệu hóa nút khi đang loading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Create Task")
                }
            }
        }
    }
}