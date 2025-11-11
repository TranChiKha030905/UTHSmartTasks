package com.uth.smarttasks.ui.screens.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Import tất cả icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
// import androidx.lifecycle.viewmodel.compose.viewModel // <-- BỎ DÒNG NÀY
import androidx.navigation.NavController
import com.uth.smarttasks.ui.navigation.Screen
import com.uth.smarttasks.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    // --- SỬA Ở ĐÂY ---
    // Nhận authViewModel từ AppNavigation, không tự tạo mới
    authViewModel: AuthViewModel
) {
    // Giờ 'authViewModel' này là cái của Activity, có chứa currentUser
    val currentUser by authViewModel.currentUser.collectAsState()
    val profileState by authViewModel.profileUiState.collectAsState()
    val context = LocalContext.current

    var showCameraExplanation by remember { mutableStateOf(false) }
    var showNotificationExplanation by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Camera permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Camera permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Notification permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notification permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

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
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .padding(top = 20.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nó sẽ hiển thị lại ở đây
            Text(
                text = currentUser?.email ?: "Not Logged In",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nó sẽ hiển thị lại ở đây
            Text(
                text = "UID: ${currentUser?.uid ?: "N/A"}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))
            Divider()

            ProfileMenuItem(
                icon = Icons.Default.Palette,
                text = "App Theme",
                onClick = {
                    navController.navigate(Screen.Theme.route)
                }
            )

            ProfileMenuItem(
                icon = Icons.Default.CameraAlt,
                text = "Change Avatar",
                onClick = {
                    checkAndRequestPermission(
                        context = context,
                        permission = Manifest.permission.CAMERA,
                        launcher = cameraLauncher,
                        onShowExplanation = { showCameraExplanation = true }
                    )
                }
            )

            ProfileMenuItem(
                icon = Icons.Default.Notifications,
                text = "Enable Reminders",
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        checkAndRequestPermission(
                            context = context,
                            permission = Manifest.permission.POST_NOTIFICATIONS,
                            launcher = notificationLauncher,
                            onShowExplanation = { showNotificationExplanation = true }
                        )
                    } else {
                        Toast.makeText(context, "Notifications already enabled", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            ProfileMenuItem(
                icon = Icons.Default.LockReset,
                text = "Reset Password",
                onClick = { authViewModel.sendPasswordReset() }
            )

            ProfileMenuItem(
                icon = Icons.Default.ExitToApp,
                text = "Log Out",
                color = MaterialTheme.colorScheme.error,
                onClick = {
                    authViewModel.signOut()
                }
            )
        }
    }

    // Dialog giải thích cho Camera
    if (showCameraExplanation) {
        PermissionExplanationDialog(
            onDismiss = { showCameraExplanation = false },
            onConfirm = {
                cameraLauncher.launch(Manifest.permission.CAMERA)
                showCameraExplanation = false
            },
            permissionName = "Camera"
        )
    }

    // Dialog giải thích cho Notification
    if (showNotificationExplanation) {
        PermissionExplanationDialog(
            onDismiss = { showNotificationExplanation = false },
            onConfirm = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                showNotificationExplanation = false
            },
            permissionName = "Notifications"
        )
    }
}

// Hàm helper để check quyền (Dùng chung)
private fun checkAndRequestPermission(
    context: Context,
    permission: String,
    launcher: androidx.activity.result.ActivityResultLauncher<String>,
    onShowExplanation: () -> Unit
) {
    when {
        // 1. Đã có quyền
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED -> {
            Toast.makeText(context, "$permission Granted", Toast.LENGTH_SHORT).show()
        }
        // 2. Chưa có quyền -> Hiện dialog giải thích
        else -> {
            onShowExplanation()
        }
    }
}

// Composable cho Dialog giải thích (Dùng chung)
@Composable
fun PermissionExplanationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    permissionName: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Required") },
        text = { Text("We need the $permissionName permission to use this feature. Please grant the permission.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Allow")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


// Composable ProfileMenuItem
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