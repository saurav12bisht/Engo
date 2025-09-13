package com.project.engo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.project.engo.chat_screen.ChatScreen
import com.project.engo.home.HomeScreen
import com.project.engo.login.LoginScreen
import com.project.engo.profile.ProfileScreen
import com.project.engo.splash.SplashScreen

// Step 1: Define Routes
sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Home : Screen("home_screen")
    object Profile : Screen("profile_screen")
    object Login : Screen("login_screen")
    object Chat : Screen("chat/{chatUserId}")

}

// Step 2: Navigation Graph
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, FirebaseAuth.getInstance())
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("chatUserId") { type = NavType.StringType })
        ) { backStackEntry ->
            ChatScreen(
                navController = navController,
                chatUserId = backStackEntry.arguments?.getString("chatUserId") ?: ""
            )
        }
    }
}
