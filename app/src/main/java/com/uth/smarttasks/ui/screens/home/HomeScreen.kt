package com.uth.smarttasks.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(homeButtons.size) { index ->
                    HomeButton(info = homeButtons[index], navController = navController)
                }
            }
        }
    }
}

// Composable cho 1 cái nút (ĐÃ SỬA LOGIC ONCLICK)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeButton(info: HomeButtonInfo, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Cho nó "fit" (tự co giãn)
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = {
            // --- SỬA LOGIC Ở ĐÂY ---
            // Dùng y hệt logic của BottomNavBar
            info.route?.let { route ->
                navController.navigate(route) {
                    // Pop up về 'home' (màn hình gốc)
                    navController.graph.startDestinationRoute?.let { startRoute ->
                        popUpTo(startRoute) {
                            saveState = true // Lưu state của 'home'
                        }
                    }
                    launchSingleTop = true // Không tạo 2 bản copy
                    restoreState = true // Phục hồi state
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
                fontSize = 16.sp
            )
        }
    }
}