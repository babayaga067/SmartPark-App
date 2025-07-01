package com.example.smartpark.navigation



import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartpark.ui_component.SplashScreen
import com.example.smartpark.view.AddEditSpotScreen
import com.example.smartpark.view.ForgotPasswordScreen
import com.example.smartpark.view.LoginScreen
//import com.example.smart park.view.ParkingSpotListScreen
import com.example.smartpark.view.RegisterScreen


@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("forgot") { ForgotPasswordScreen(navController) }
        }
        //composable("Home"){ParkingSpotListScreen(navController)}


}
