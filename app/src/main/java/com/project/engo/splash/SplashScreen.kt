package com.project.engo.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.project.engo.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    var loadingText by remember { mutableStateOf("Initializing...") }
    var progress by remember { mutableStateOf(0f) }

    // Animation values
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val floatingOffset1 = remember { Animatable(0f) }
    val floatingOffset2 = remember { Animatable(0f) }
    val floatingOffset3 = remember { Animatable(0f) }

    // Start animations
    LaunchedEffect(Unit) {
        // Logo entrance animation
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(800)
        )

        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800)
        )

        delay(300)

        // Text fade in
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600)
        )

        // Start floating animations
        floatingOffset1.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        floatingOffset2.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        floatingOffset3.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Splash logic with loading states
    LaunchedEffect(Unit) {
        // Loading sequence
        loadingText = "Loading ENGO..."
        progress = 0.2f
        delay(1500)

        loadingText = "Setting up your workspace..."
        progress = 0.5f
        delay(1500)

        loadingText = "Preparing learning materials..."
        progress = 0.8f
        delay(1500)

        loadingText = "Almost ready..."
        progress = 1f
        delay(1500)

        // Navigation logic
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6366F1), // Indigo
                        Color(0xFF8B5CF6), // Purple
                        Color(0xFFEC4899), // Pink
                        Color(0xFF06B6D4)  // Cyan
                    ),
                    radius = 1200f
                )
            )
    ) {
        // Floating background elements
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(
                    x = 50.dp + (floatingOffset1.value * 30).dp,
                    y = 100.dp + (floatingOffset1.value * 40).dp
                )
                .background(
                    Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(
                    x = 300.dp + (floatingOffset2.value * -25).dp,
                    y = 200.dp + (floatingOffset2.value * 35).dp
                )
                .background(
                    Color.White.copy(alpha = 0.08f),
                    RoundedCornerShape(20.dp)
                )
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(
                    x = 80.dp + (floatingOffset3.value * 20).dp,
                    y = 500.dp + (floatingOffset3.value * -30).dp
                )
                .background(
                    Color.White.copy(alpha = 0.12f),
                    CircleShape
                )
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo/Title
            Box(
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ENGO",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 4.sp,
                            fontSize = 56.sp
                        )
                    )

                    Text(
                        text = "English Learning Platform",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Tagline
            Column(
                modifier = Modifier.alpha(textAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Master English",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = "One conversation at a time",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Loading indicator
            Column(
                modifier = Modifier.alpha(textAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .size(width = 200.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = loadingText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom decorative text
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(textAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Powered by AI • Built for Success",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}