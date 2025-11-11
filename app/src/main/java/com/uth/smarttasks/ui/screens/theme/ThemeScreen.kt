package com.uth.smarttasks.ui.screens.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uth.smarttasks.SmartTasksApplication
import com.uth.smarttasks.ui.theme.BlueColorScheme
import com.uth.smarttasks.ui.theme.DarkColorScheme
import com.uth.smarttasks.ui.theme.LightColorScheme
import com.uth.smarttasks.ui.viewmodel.ThemeViewModel
import com.uth.smarttasks.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    navController: NavController,
    factory: ViewModelFactory
) {
    // Lấy ThemeViewModel qua factory
    val viewModel: ThemeViewModel = viewModel(factory = factory)

    // Lấy theme hiện tại ("Light", "Dark", "Blue")
    val currentTheme by viewModel.theme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Theme", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lựa chọn 1: Trắng (Light)
            ThemeOption(
                themeName = "Light",
                colorScheme = LightColorScheme,
                isSelected = currentTheme == "Light",
                onClick = { viewModel.setTheme("Light") }
            )

            // Lựa chọn 2: Đen (Dark)
            ThemeOption(
                themeName = "Dark",
                colorScheme = DarkColorScheme,
                isSelected = currentTheme == "Dark",
                onClick = { viewModel.setTheme("Dark") }
            )

            // Lựa chọn 3: Xanh (Blue)
            ThemeOption(
                themeName = "Blue",
                colorScheme = BlueColorScheme,
                isSelected = currentTheme == "Blue",
                onClick = { viewModel.setTheme("Blue") }
            )
        }
    }
}

// Composable cho 1 cái ô chọn màu
@Composable
fun ThemeOption(
    themeName: String,
    colorScheme: ColorScheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tên
            Text(
                text = themeName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // 3 ô màu preview
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ColorCircle(color = colorScheme.background) // Nền
                ColorCircle(color = colorScheme.primary) // Chính
                ColorCircle(color = colorScheme.secondary) // Phụ
            }

            // Dấu tick
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

// Composable cho 1 cái hình tròn màu
@Composable
fun ColorCircle(color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), CircleShape)
    )
}