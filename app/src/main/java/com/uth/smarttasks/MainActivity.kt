package com.uth.smarttasks

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseUser
import com.uth.smarttasks.ui.navigation.BottomNavBar
import com.uth.smarttasks.ui.navigation.Screen
import com.uth.smarttasks.ui.screens.calendar.CalendarScreen
import com.uth.smarttasks.ui.screens.create.CreateTaskScreen
import com.uth.smarttasks.ui.screens.detail.TaskDetailScreen
import com.uth.smarttasks.ui.screens.home.HomeScreen
import com.uth.smarttasks.ui.screens.list.TaskListScreen
import com.uth.smarttasks.ui.screens.login.LoginScreen
import com.uth.smarttasks.ui.screens.profile.ProfileScreen
import com.uth.smarttasks.ui.screens.projects.ProjectsScreen
import com.uth.smarttasks.ui.screens.statistics.StatisticsScreen
import com.uth.smarttasks.ui.screens.theme.ThemeScreen
import com.uth.smarttasks.ui.screens.welcome.WelcomeScreen
import com.uth.smarttasks.ui.theme.UTHSmartTasksTheme
import com.uth.smarttasks.ui.viewmodel.AuthViewModel // <-- Import
import com.uth.smarttasks.ui.viewmodel.ThemeViewModel
import com.uth.smarttasks.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    // AuthViewModel của Activity (chứa user)
    private val authViewModel: AuthViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                authViewModel.isLoadingFromSplash.value
            }
        }

        setContent {
            val application = LocalContext.current.applicationContext as SmartTasksApplication

            // Lấy ThemeViewModel (dùng Factory)
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ViewModelFactory(application)
            )

            val themeName by themeViewModel.theme.collectAsState()

            // Lấy currentUser (từ AuthViewModel của Activity)
            val currentUser by authViewModel.currentUser.collectAsState()

            UTHSmartTasksTheme(themeName = themeName) {
                MainApp(
                    currentUser = currentUser,
                    // --- SỬA Ở ĐÂY ---
                    authViewModel = authViewModel, // <-- Truyền authViewModel
                    factory = ViewModelFactory(application)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    currentUser: FirebaseUser?,
    authViewModel: AuthViewModel, // <-- Nhận authViewModel
    factory: ViewModelFactory
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.TaskList.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        val startDestination = if (currentUser != null) {
            Screen.Home.route
        } else {
            Screen.Welcome.route
        }

        AppNavigation(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            authViewModel = authViewModel, // <-- Truyền authViewModel xuống
            factory = factory
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel, // <-- Nhận authViewModel
    factory: ViewModelFactory
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            // LoginScreen tự tạo AuthViewModel riêng (của nó)
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.TaskList.route) {
            TaskListScreen(navController = navController, factory = factory)
        }

        // --- SỬA Ở ĐÂY ---
        composable(Screen.Profile.route) {
            // Truyền authViewModel (của Activity) vào
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            if (taskId != null) {
                TaskDetailScreen(navController = navController, taskId = taskId, factory = factory)
            }
        }
        composable(Screen.CreateTask.route) {
            CreateTaskScreen(navController = navController, factory = factory)
        }
        composable(Screen.Projects.route) {
            ProjectsScreen(navController = navController, factory = factory)
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(navController = navController, factory = factory)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController = navController, factory = factory)
        }
        composable(Screen.Theme.route) {
            ThemeScreen(navController = navController, factory = factory)
        }
    }
}