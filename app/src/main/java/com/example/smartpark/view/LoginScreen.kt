package com.example.smartpark.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartpark.viewmodel.LoginState
import com.example.smartpark.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {

    // Input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    // Handle login state changes from ViewModel
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Loading -> isLoading = true

            is LoginState.Success -> {
                isLoading = false
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                viewModel.resetState()

                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }

            is LoginState.Error -> {
                isLoading = false
                val message = (loginState as LoginState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Email input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = viewModel.emailError.value != null,
            modifier = Modifier.fillMaxWidth()
        )
        viewModel.emailError.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        // Password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            isError = viewModel.passwordError.value != null,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        viewModel.passwordError.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(
            onClick = {
                if (viewModel.validateInputs(email, password)) {
                    viewModel.loginUser(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Logging in..." else "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Register + Forgot Password buttons
        TextButton(onClick = { navController.navigate("register") }) {
            Text("Don't have an account? Register")
        }

        TextButton(onClick = { navController.navigate("forgot") }) {
            Text("Forgot Password?")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Preview doesn't support NavController easily
    // So we can leave it out or mock it
    // LoginScreen(navController = rememberNavController())
}
