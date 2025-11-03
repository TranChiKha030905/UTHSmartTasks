package com.uth.smarttasks.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
// import com.uth.smarttasks.ui.navigation.Screen // Dòng này không cần nữa
import com.uth.smarttasks.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel() // Lấy AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val profileState by authViewModel.profileUiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = profileState.message) {
        if (profileState.message != null) {
            Toast.makeText(context, profileState.message, Toast.LENGTH_LONG).show()
            authViewModel.resetProfileMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Avatar (Icon)
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .padding(top = 20.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Email
            Text(
                text = currentUser?.email ?: "Not Logged In",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. User ID
            Text(
                text = "UID: ${currentUser?.uid ?: "N/A"}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))
            Divider()

            // 4. Các nút chức năng

            // Nút Reset Password
            ProfileMenuItem(
                icon = Icons.Default.LockReset,
                text = "Reset Password",
                onClick = {
                    authViewModel.sendPasswordReset()
                }
            )

            // Nút Log Out
            ProfileMenuItem(
                icon = Icons.Default.ExitToApp,
                text = "Log Out",
                color = MaterialTheme.colorScheme.error,
                onClick = {
                    // --- SỬA Ở ĐÂY ---
                    // Chỉ cần gọi signOut().
                    // MainActivity sẽ tự động "phản ứng" và đưa về Welcome.
                    authViewModel.signOut()

                    /* XÓA DÒNG NÀY (Dòng gây giật):
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0)
                    }
                    */
                }
            )
        }
    }
}

// Composable riêng cho 1 item trong menu (Giữ nguyên)
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = color,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Go",
                modifier = Modifier.size(16.dp),
                tint = color
            )
        }
        Divider()
    }
}