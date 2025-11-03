package com.uth.smarttasks.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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

// Data class cho 4 cái nút
data class HomeButtonInfo(
    val title: String,
    val iconResId: Int,
    val route: String? = null // route để navigate
)

// List 4 nút
val homeButtons = listOf(
    HomeButtonInfo("My Tasks", R.drawable.ic_home_my_tasks, Screen.TaskList.route),
    HomeButtonInfo("Projects", R.drawable.ic_home_projects),
    HomeButtonInfo("Calendar", R.drawable.ic_home_calendar),
    HomeButtonInfo("Statistics", R.drawable.ic_home_statistics)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // HomeScreen bây giờ có TopAppBar riêng
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

            // Lưới 2x2
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

// Composable cho 1 cái nút
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeButton(info: HomeButtonInfo, navController: NavController) {
    Card(
        modifier = Modifier
            .size(160.dp), // Kích thước ô vuông
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = {
            // Nếu nút có route, navigate tới đó
            info.route?.let {
                navController.navigate(it)
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
                modifier = Modifier.size(60.dp) // Kích thước icon
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