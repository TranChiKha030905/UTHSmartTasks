package com.uth.smarttasks.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uth.smarttasks.R
import com.uth.smarttasks.ui.navigation.Screen

// Data class (Giữ nguyên)
data class HomeButtonInfo(
    val title: String,
    val iconResId: Int,
    val route: String? = null
)

// List 4 nút (Giữ nguyên)
val homeButtons = listOf(
    HomeButtonInfo("My Tasks", R.drawable.ic_home_my_tasks, Screen.TaskList.route),
    HomeButtonInfo("Projects", R.drawable.ic_home_projects, Screen.Projects.route),
    HomeButtonInfo("Calendar", R.drawable.ic_home_calendar, Screen.Calendar.route),
    HomeButtonInfo("Statistics", R.drawable.ic_home_statistics, Screen.Statistics.route)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "UTH SmartTasks",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->

        // --- SỬA Ở ĐÂY ---
        // Column ngoài cùng
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            // THÊM 2 DÒNG NÀY ĐỂ CĂN GIỮA
            verticalArrangement = Arrangement.Center, // Căn giữa theo chiều dọc
            horizontalAlignment = Alignment.Start     // Căn lề trái cho Text
        ) {
            Text(
                text = "Welcome back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Column chứa 2 Row (4 nút)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hàng 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HomeButton(
                        info = homeButtons[0],
                        navController = navController,
                        modifier = Modifier.weight(1f)
                    )
                    HomeButton(
                        info = homeButtons[1],
                        navController = navController,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Hàng 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HomeButton(
                        info = homeButtons[2],
                        navController = navController,
                        modifier = Modifier.weight(1f)
                    )
                    HomeButton(
                        info = homeButtons[3],
                        navController = navController,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Composable cho 1 cái nút (Giữ nguyên, đã "đẹp")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeButton(
    info: HomeButtonInfo,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1f), // "Fit" (luôn vuông)
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Màu trắng
        ),
        onClick = {
            // Logic điều hướng "xịn"
            info.route?.let { route ->
                navController.navigate(route) {
                    navController.graph.startDestinationRoute?.let { startRoute ->
                        popUpTo(startRoute) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = info.iconResId),
                contentDescription = info.title,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = info.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}