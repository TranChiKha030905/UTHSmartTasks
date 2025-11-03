package com.uth.smarttasks.ui.screens.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Data class "giả" cho 1 Project (ĐÃ XÓA 'private')
data class Project(
    val id: Int,
    val title: String,
    val description: String,
    val status: String
)

// Danh sách Project "giả" (Giữ nguyên)
private val fakeProjects = listOf(
    Project(1, "UTH SmartTasks App", "Hoàn thiện app quản lý công việc", "In Progress"),
    Project(2, "Website Bán hàng", "Xây dựng E-commerce bằng React", "Pending"),
    Project(3, "Báo cáo Tốt nghiệp", "Viết và bảo vệ luận án", "Completed")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projects", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // Nút back để quay về HomeScreen
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Danh sách các Project
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(fakeProjects) { project ->
                ProjectItemCard(project = project)
            }
        }
    }
}

// Composable cho 1 cái thẻ Project
@Composable
fun ProjectItemCard(project: Project) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = project.status,
                style = MaterialTheme.typography.labelMedium,
                color = when (project.status.lowercase()) {
                    "in progress" -> Color(0xFFFFA500) // Cam
                    "pending" -> Color.Red
                    "completed" -> Color.Green
                    else -> Color.Gray
                }
            )
        }
    }
}