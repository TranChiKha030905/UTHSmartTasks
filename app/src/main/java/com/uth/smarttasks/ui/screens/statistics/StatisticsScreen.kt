package com.uth.smarttasks.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Data class "giả" cho 1 mục thống kê (ĐÃ XÓA 'private')
data class StatItem(
    val icon: ImageVector?,
    val label: String,
    val value: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Dùng LazyColumn để có thể cuộn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Thẻ thống kê 1: Tổng quan
            item {
                StatisticCard(
                    title = "Tasks Overview",
                    items = listOf(
                        StatItem(Icons.Default.DonutSmall, "Total Tasks", "8", Color.Blue),
                        StatItem(Icons.Default.CheckCircle, "Completed", "5", Color.Green),
                        StatItem(Icons.Default.Pending, "Pending", "3", Color.Red)
                    )
                )
            }

            // Thẻ thống kê 2: Theo mức độ ưu tiên
            item {
                StatisticCard(
                    title = "By Priority",
                    items = listOf(
                        StatItem(null, "High", "2", Color.Red),
                        StatItem(null, "Medium", "4", Color(0xFFFFA500)), // Cam
                        StatItem(null, "Low", "2", Color.Gray)
                    )
                )
            }

            // Thẻ thống kê 3: Theo danh mục (Fake)
            item {
                StatisticCard(
                    title = "By Category",
                    items = listOf(
                        StatItem(null, "Work", "5", Color.Blue),
                        StatItem(null, "Personal", "2", Color.Green),
                        StatItem(null, "Study", "1", Color.Magenta)
                    )
                )
            }
        }
    }
}

// Composable cho 1 cái thẻ thống kê
@Composable
fun StatisticCard(title: String, items: List<StatItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.icon != null) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = item.color,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = item.value,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = item.color,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}