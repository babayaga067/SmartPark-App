package com.example.smartpark.view

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.smartpark.viewmodel.ProfileState
import com.example.smartpark.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: androidx.navigation.NavHostController? = null,
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.profileState.collectAsState()
    val name by viewModel.name.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val nameError by viewModel.nameError.collectAsState()
    val phoneError by viewModel.phoneError.collectAsState()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    // Handle all navigation/feedback
    LaunchedEffect(state) {
        when (state) {
            is ProfileState.Success -> {
                val user = (state as ProfileState.Success).user
                viewModel.onNameChange(user.name)
                viewModel.onPhoneChange(user.phone)
            }
            is ProfileState.Error -> {
                Toast.makeText(context, (state as ProfileState.Error).message, Toast.LENGTH_LONG).show()
            }
            is ProfileState.UpdateSuccess -> {
                Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is ProfileState.UpdateError -> {
                Toast.makeText(context, (state as ProfileState.UpdateError).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            is ProfileState.LoggedOut -> {
                Toast.makeText(context, "Logged out.", Toast.LENGTH_SHORT).show()
                navController?.navigate("login") { popUpTo("profile") { inclusive = true } }
            }
            is ProfileState.Deleted -> {
                Toast.makeText(context, "Account deleted.", Toast.LENGTH_SHORT).show()
                navController?.navigate("login") { popUpTo(0) }
            }
            else -> {}
        }
    }

    // SmartPark blue/white theme
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF8FAFF), Color(0xFFF0F3FA), Color(0xFFFFFFFF)
        ),
        startY = 0f, endY = 1200f
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .systemBarsPadding()
    ) {
        if (state is ProfileState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF1976D2)
            )
        }

        // -- The Card Form (centered/scrollable) --
        if (
            state is ProfileState.Success ||
            state is ProfileState.Idle ||
            state is ProfileState.UpdateSuccess ||
            state is ProfileState.UpdateError
        ) {

            val user = if (state is ProfileState.Success) (state as ProfileState.Success).user else null
            val profileUrl = user?.profileImageUrl ?: ""
            // Responsive, always scrollable if small device
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    shadowElevation = 10.dp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(min = 290.dp, max = 410.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp, bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 34.dp, horizontal = 20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar with blue border and editable effect
                        Box(
                            Modifier
                                .size(108.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1976D2).copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileUrl.isNotBlank()) {
                                AsyncImage(
                                    model = profileUrl,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .size(104.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color(0xFFBCCCED),
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = user?.email ?: "No email",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(21.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.onNameChange(it) },
                            label = { Text("Full Name") },
                            isError = nameError != null,
                            singleLine = true,
                            shape = RoundedCornerShape(28.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1976D2),
                                unfocusedBorderColor = Color(0xFFE3EAFE),
                                errorBorderColor = Color(0xFFD82342),
                                cursorColor = Color(0xFF1976D2),
                                focusedContainerColor = Color.White.copy(alpha = 0.85f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 17.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 60.dp, max = 70.dp)
                        )
                        AnimatedVisibility(nameError != null) {
                            Text(
                                nameError.orEmpty(),
                                color = Color(0xFFD82342),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 9.dp, top = 2.dp, bottom = 2.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { viewModel.onPhoneChange(it) },
                            label = { Text("Phone") },
                            isError = phoneError != null,
                            singleLine = true,
                            shape = RoundedCornerShape(28.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1976D2),
                                unfocusedBorderColor = Color(0xFFE3EAFE),
                                errorBorderColor = Color(0xFFD82342),
                                cursorColor = Color(0xFF1976D2),
                                focusedContainerColor = Color.White.copy(alpha = 0.85f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 17.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 60.dp, max = 70.dp)
                        )
                        AnimatedVisibility(phoneError != null) {
                            Text(
                                phoneError.orEmpty(),
                                color = Color(0xFFD82342),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 9.dp, top = 2.dp, bottom = 2.dp)
                            )
                        }

                        Spacer(Modifier.height(22.dp))
                        Button(
                            onClick = {
                                viewModel.validateAndUpdateProfile()
                            },
                            enabled = !name.isBlank() && !phone.isBlank(),
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Save Changes", fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(Modifier.height(28.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1976D2)),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                                Spacer(Modifier.width(7.dp))
                                Text("Logout", fontWeight = FontWeight.Medium)
                            }
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                                Spacer(Modifier.width(7.dp))
                                Text("Delete Account", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        // --- Confirm Delete Dialog ---
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Account") },
                text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteAccount()
                        }
                    ) { Text("Delete", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) { Text("Cancel") }
                }
            )
        }
    }
}
