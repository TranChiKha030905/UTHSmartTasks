package com.uth.smarttasks.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uth.smarttasks.ui.navigation.Screen
import com.uth.smarttasks.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // True = Đăng nhập, False = Đăng ký
    var isLoginMode by remember { mutableStateOf(true) }

    val uiState = authViewModel.uiState.collectAsState().value
    val context = LocalContext.current

    // Xử lý khi có lỗi
    LaunchedEffect(key1 = uiState.error) {
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            authViewModel.resetError()
        }
    }

    // Xử lý khi thành công
    LaunchedEffect(key1 = uiState.success) {
        if (uiState.success) {
            // Đăng nhập/Đăng ký thành công -> vào Home
            navController.navigate(Screen.Home.route) {
                // Xóa toàn bộ backstack
                popUpTo(0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Log In" else "Sign Up",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Nút bấm
        Button(
            onClick = {
                if (isLoginMode) {
                    authViewModel.signIn(email, password)
                } else {
                    authViewModel.signUp(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(if (isLoginMode) "Log In" else "Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text đổi chế độ
        Text(
            text = if (isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Log In",
            modifier = Modifier.clickable { isLoginMode = !isLoginMode },
            textAlign = TextAlign.Center
        )
    }
}