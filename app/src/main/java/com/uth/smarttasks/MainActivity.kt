package com.uth.smarttasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.uth.smarttasks.ui.screens.create.CreateTaskScreen
import com.uth.smarttasks.ui.screens.detail.TaskDetailScreen
import com.uth.smarttasks.ui.screens.home.HomeScreen
import com.uth.smarttasks.ui.screens.list.TaskListScreen
import com.uth.smarttasks.ui.screens.login.LoginScreen
import com.uth.smarttasks.ui.screens.profile.ProfileScreen
import com.uth.smarttasks.ui.screens.welcome.WelcomeScreen
import com.uth.smarttasks.ui.theme.UTHSmartTasksTheme
import com.uth.smarttasks.ui.viewmodel.AuthViewModel
import com.uth.smarttasks.ui.screens.projects.ProjectsScreen
import com.uth.smarttasks.ui.screens.calendar.CalendarScreen
import com.uth.smarttasks.ui.screens.statistics.StatisticsScreen
class MainActivity : ComponentActivity() {

    // Lấy AuthViewModel ở cấp Activity
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cài đặt Splash Screen
        installSplashScreen().apply {
            // SỬA LẠI LOGIC:
            // Giữ splash screen "CHO ĐẾN KHI" ViewModel báo là 'isLoading' = false
            setKeepOnScreenCondition {
                authViewModel.isLoadingFromSplash.value
            }
        }

        setContent {
            UTHSmartTasksTheme {
                // Lấy user hiện tại từ ViewModel
                // (ViewModel bây giờ đã tự động cập nhật)
                val currentUser by authViewModel.currentUser.collectAsState()
                MainApp(currentUser = currentUser)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(currentUser: FirebaseUser?) {
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
        // Quyết định màn hình bắt đầu dựa trên currentUser
        val startDestination = if (currentUser != null) {
            Screen.Home.route // Đã login -> vào Home
        } else {
            Screen.Welcome.route // Chưa login -> vào Welcome
        }

        AppNavigation(
            navController = navController,
            startDestination = startDestination, // Truyền vào
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// AppNavigation KHÔNG CẦN THAY ĐỔI
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
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
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.TaskList.route) {
            TaskListScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            if (taskId != null) {
                TaskDetailScreen(navController = navController, taskId = taskId)
            }
        }
        composable(Screen.CreateTask.route) {
            CreateTaskScreen(navController = navController)
        }
        composable(Screen.Projects.route) {
            ProjectsScreen(navController = navController)
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(navController = navController)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController = navController)
        }
    }
}