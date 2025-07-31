package com.example.smartpark.view.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.smartpark.viewmodel.LoginState
import com.example.smartpark.viewmodel.LoginViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val isLoading = loginState is LoginState.Loading
    val isDark = isSystemInDarkTheme()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFF8FAFF), Color(0xFFF0F3FA), Color(0xFFFFFFFF)),
        startY = 0f, endY = 2000f
    )

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                val isAdmin = (loginState as LoginState.Success).isAdmin
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(if (isAdmin) "adminDashboard" else "userDashboard") {
                    popUpTo("login") { inclusive = true }
                }
                viewModel.resetState()
            }
            is LoginState.Error -> {
                Toast.makeText(context, (loginState as LoginState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 380.dp)
                    .shadow(10.dp, RoundedCornerShape(32.dp), ambientColor = Color(0x220C53D5)),
                shape = RoundedCornerShape(32.dp),
                color = if (isDark) Color.White.copy(alpha = 0.98f) else Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1976D2).copy(alpha = 0.13f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalParking,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    val appearAnim = remember { Animatable(-20f) }
                    LaunchedEffect(Unit) { appearAnim.animateTo(0f, tween(800, easing = EaseOutCubic)) }
                    Text(
                        "Login to SmartPark",
                        color = Color(0xFF1976D2),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                        letterSpacing = 1.1.sp,
                        modifier = Modifier
                            .offset(y = appearAnim.value.dp)
                            .graphicsLayer { alpha = 1f - abs(appearAnim.value) / 20f }
                            .padding(horizontal = 1.dp)
                    )
                    Text(
                        "Secure, simple parking for everyone.",
                        color = Color.Gray.copy(alpha = 0.66f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                    Spacer(Modifier.height(10.dp))

                    FrostedTextField(
                        label = "Email", value = email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        enabled = !isLoading,
                        isError = emailError != null,
                        keyboardType = KeyboardType.Email,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1976D2)) }
                    )
                    ShowErrorAnim(emailError)
                    FrostedPasswordField(
                        value = password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        visible = isPasswordVisible,
                        onVisibilityChange = { viewModel.togglePasswordVisibility() },
                        enabled = !isLoading,
                        isError = passwordError != null
                    )
                    ShowErrorAnim(passwordError)
                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.login() },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .graphicsLayer { shadowElevation = 8f }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(
                            "Login",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.6.sp
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { navController.navigate("forgotPassword") },
                            enabled = !isLoading,
                            contentPadding = PaddingValues(horizontal = 0.dp)
                        ) {
                            Text(
                                "Forgot password?",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        TextButton(
                            onClick = { navController.navigate("register") },
                            enabled = !isLoading,
                            contentPadding = PaddingValues(horizontal = 0.dp)
                        ) {
                            Text(
                                "Register",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// FrostedTextField, FrostedPasswordField, ShowErrorAnim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrostedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(fontSize = 17.sp),
        enabled = enabled,
        label = { Text(label, fontWeight = FontWeight.Medium, color = Color(0xFF8AA9D6)) },
        leadingIcon = leadingIcon,
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1976D2),
            unfocusedBorderColor = Color(0xFFE3EAFE),
            errorBorderColor = Color(0xFFD82342),
            cursorColor = Color(0xFF1976D2),
            focusedContainerColor = Color.White.copy(alpha = 0.75f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.55f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp, max = 70.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrostedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onVisibilityChange: () -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String = "Password"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontWeight = FontWeight.Medium, color = Color(0xFF8AA9D6)) },
        singleLine = true,
        isError = isError,
        enabled = enabled,
        textStyle = LocalTextStyle.current.copy(fontSize = 17.sp),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (visible) "Hide password" else "Show password",
                    tint = Color(0xFF1976D2)
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1976D2),
            unfocusedBorderColor = Color(0xFFE3EAFE),
            errorBorderColor = Color(0xFFD82342),
            cursorColor = Color(0xFF1976D2),
            focusedContainerColor = Color.White.copy(alpha = 0.75f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.55f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp, max = 70.dp)
    )
}

@Composable
private fun ShowErrorAnim(text: String?) {
    AnimatedVisibility(text != null) {
        Text(
            text = text ?: "",
            color = Color(0xFFD82342),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 9.dp, top = 2.dp, bottom = 2.dp)
                .wrapContentWidth(Alignment.Start)
        )
    }
}
