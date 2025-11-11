package com.uth.smarttasks.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uth.smarttasks.SmartTasksApplication
import com.uth.smarttasks.ui.viewmodel.StatsData
import com.uth.smarttasks.ui.viewmodel.StatisticsViewModel
import com.uth.smarttasks.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    factory: ViewModelFactory // <-- Nhận factory từ AppNavigation
) {
    // --- Gọi ViewModel qua Factory ---
    val viewModel: StatisticsViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadStats() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.error != null -> {
                    Text(text = "Lỗi: ${uiState.error}", color = Color.Red)
                }
                else -> {
                    StatisticsContent(stats = uiState.stats)
                }
            }
        }
    }
}

@Composable
fun StatisticsContent(stats: StatsData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatisticCard(
                title = "Tasks Overview",
                items = listOf(
                    StatItem(Icons.Default.DonutSmall, "Total Tasks", stats.totalTasks.toString(), Color.Blue),
                    StatItem(Icons.Default.CheckCircle, "Completed", stats.completedTasks.toString(), Color.Green),
                    StatItem(Icons.Default.Pending, "Pending", stats.pendingTasks.toString(), Color.Red)
                )
            )
        }

        item {
            StatisticCard(
                title = "By Priority",
                items = stats.byPriority.map { (priority, count) ->
                    StatItem(
                        icon = null,
                        label = priority,
                        value = count.toString(),
                        color = when (priority.lowercase()) {
                            "high" -> Color.Red
                            "medium" -> Color(0xFFFFA500)
                            else -> Color.Gray
                        }
                    )
                }
            )
        }

        item {
            StatisticCard(
                title = "By Category",
                items = stats.byCategory.map { (category, count) ->
                    StatItem(null, category, count.toString(), Color.Blue)
                }
            )
        }
    }
}

data class StatItem(
    val icon: ImageVector?,
    val label: String,
    val value: String,
    val color: Color
)

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