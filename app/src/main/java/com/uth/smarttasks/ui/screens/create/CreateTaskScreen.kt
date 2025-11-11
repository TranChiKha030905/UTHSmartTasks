package com.uth.smarttasks.ui.screens.create

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uth.smarttasks.SmartTasksApplication
import com.uth.smarttasks.ui.screens.profile.PermissionExplanationDialog
import com.uth.smarttasks.ui.viewmodel.CreateTaskViewModel
import com.uth.smarttasks.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    navController: NavController,
    factory: ViewModelFactory // <-- Nhận factory từ AppNavigation
) {
    // --- Gọi ViewModel qua Factory ---
    val viewModel: CreateTaskViewModel = viewModel(factory = factory)

    val uiState = viewModel.uiState.value
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    var showLocationExplanation by remember { mutableStateOf(false) }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Location permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Location permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(key1 = uiState.creationSuccess) {
        if (uiState.creationSuccess) {
            Toast.makeText(context, "Task created!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(key1 = uiState.error) {
        if(uiState.error != null) {
            Toast.makeText(context, "Error: ${uiState.error}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Task", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (e.g., Work, Personal)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = priority,
                onValueChange = { priority = it },
                label = { Text("Priority (e.g., High, Medium, Low)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Due Date (e.g., 2025-11-05)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = {
                    checkAndRequestPermission(
                        context = context,
                        permission = Manifest.permission.ACCESS_FINE_LOCATION,
                        launcher = locationLauncher,
                        onShowExplanation = { showLocationExplanation = true }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Add Location", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Location")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.createTask(title, description, category, priority, dueDate)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Create Task")
                }
            }
        }
    }

    if (showLocationExplanation) {
        PermissionExplanationDialog(
            onDismiss = { showLocationExplanation = false },
            onConfirm = {
                locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                showLocationExplanation = false
            },
            permissionName = "Location"
        )
    }
}

private fun checkAndRequestPermission(
    context: Context,
    permission: String,
    launcher: androidx.activity.result.ActivityResultLauncher<String>,
    onShowExplanation: () -> Unit
) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED -> {
            Toast.makeText(context, "$permission Granted", Toast.LENGTH_SHORT).show()
        }
        else -> {
            onShowExplanation()
        }
    }
}