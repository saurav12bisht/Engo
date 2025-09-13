package com.project.engo.splash

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.project.engo.Screen

@Composable
fun SplashScreen(navController: NavHostController) {
    // Do your splash logic here
    // Navigate after delay
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        if (FirebaseAuth.getInstance().currentUser != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
        }

    }
    androidx.compose.material3.Text(text = "Splash Screen")
}




