package com.example.smartpark.ui_component


import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import com.example.smartpark.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource




@Composable
fun SplashScreen(navController: NavHostController) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        visible = true
        delay(3000) // Wait for 3 seconds
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Image(
                    painter = painterResource(id = R.drawable.smartpark_logo),
                    contentDescription = "SmartPark Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.Gray, CircleShape)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("SmartPark", fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}


