package com.project.engo.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


// Screens for bottom navigation
sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object ChatList : BottomNavScreen("chatlist", "Chats", Icons.Default.Send)
    object Profile : BottomNavScreen("profile", "Profile", Icons.Default.Person)
}

@Composable
fun HomeScreen(navController: NavHostController) {
    val bottomNavController = rememberNavController()

    val items = listOf(
        BottomNavScreen.ChatList,
        BottomNavScreen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavScreen.ChatList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.ChatList.route) { ChatListScreen() }
            composable(BottomNavScreen.Profile.route) { ProfileScreen() }
        }
    }
}

@Composable
fun ChatListScreen() {
    Text(text = "Chat List Screen", style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun ProfileScreen() {
    Text(text = "Profile Screen", style = MaterialTheme.typography.headlineSmall)
}
