package com.example.smartpark.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartpark.viewmodel.ForgotPasswordState
import com.example.smartpark.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val resetState by viewModel.resetState.collectAsState()

    // Reactively handle success/error/loading
    LaunchedEffect(resetState) {
        when (resetState) {
            is ForgotPasswordState.Loading -> isSending = true

            is ForgotPasswordState.Success -> {
                isSending = false
                Toast.makeText(context, "Reset email sent!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                navController.popBackStack()
            }

            is ForgotPasswordState.Error -> {
                isSending = false
                val message = (resetState as ForgotPasswordState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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
        Text("Forgot Password", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your registered email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    viewModel.sendResetEmail(email)
                } else {
                    Toast.makeText(context, "Email can't be empty", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSending
        ) {
            Text(if (isSending) "Sending..." else "Send Reset Email")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Back to Login")
        }
    }
}
