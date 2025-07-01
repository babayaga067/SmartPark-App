package com.example.smartpark.view

import com.example.smartpark.R
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartpark.viewmodel.RegisterState
import com.example.smartpark.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val registerState by viewModel.registerState.collectAsState()

    // Observe the backend state to respond in UI
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Loading -> isLoading = true

            is RegisterState.Success -> {
                isLoading = false
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }

            is RegisterState.Error -> {
                isLoading = false
                val message = (registerState as RegisterState.Error).message
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            placeholder = { Text("Enter your name") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.nameError.value != null,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            },
            singleLine = true
        )
        viewModel.nameError.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.emailError.value != null,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = null)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        viewModel.emailError.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.passwordError.value != null,
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.pass_lock), contentDescription = null)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        viewModel.passwordError.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("Re-enter password") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.confirmPasswordError.value != null,
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.pass_lock), contentDescription = null)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        viewModel.confirmPasswordError.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (viewModel.validateInputs(name, email, password, confirmPassword)) {
                    viewModel.registerUser(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Registering..." else "Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    // Normally NavController should be passed via NavHost — skip in preview
}
