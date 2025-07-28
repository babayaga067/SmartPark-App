package com.example.smartpark.view.admin

import ManageUsersViewModel
import com.example.smartpark.model.UserModel // <-- Replace or use your real path!
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    viewModel: ManageUsersViewModel
) {
    val users by viewModel.users.collectAsState(emptyList())
    var search by remember { mutableStateOf("") }
    val filtered = users.filter { it.email.contains(search, ignoreCase = true) }

    // Dialog/Edit state management
    var showEditDialog by remember { mutableStateOf<UserModel?>(null) }
    var showViewDialog by remember { mutableStateOf<UserModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf<UserModel?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFc94b4b)
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = "Add User", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFee9ca7), Color(0xFFffdde1))))
                .systemBarsPadding()
                .padding(innerPadding)
        ) {
            Text(
                "Manage Users",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFFc94b4b),
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
            )
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search by Email") },
                singleLine = true,
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            if (errorMsg != null) Text(errorMsg!!, color = Color.Red, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 16.dp))
            Text(
                "Total: ${filtered.size}",
                color = Color(0xFFf582ae),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 18.dp, bottom = 3.dp)
            )
            if (users.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { user ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(7.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Box(
                                    Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (user.role == "Admin") Color(0xFFc94b4b) else Color(0xFF4b79a1).copy(alpha = 0.21f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = if (user.role == "Admin") Color(0xFFc94b4b) else Color(0xFF4b79a1)
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        user.email, fontWeight = FontWeight.Bold,
                                        color = Color(0xFFee9ca7), style = MaterialTheme.typography.titleMedium
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Role: ", fontWeight = FontWeight.SemiBold)
                                        RoleChip(user.role)
                                    }
                                    Text("Name: ${user.name}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { showViewDialog = user }) {
                                    Icon(Icons.Filled.Info, "View", tint = Color(0xFF43cea2))
                                }
                                IconButton(onClick = { showEditDialog = user }) {
                                    Icon(Icons.Filled.Edit, "Edit", tint = Color(0xFF56ab2f))
                                }
                                IconButton(onClick = { showDeleteDialog = user }) {
                                    Icon(Icons.Filled.Delete, "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add/Edit/View/Delete Dialogs
        showViewDialog?.let { user ->
            UserDetailDialog(
                user = user,
                onClose = { showViewDialog = null }
            )
        }
        showEditDialog?.let { user ->
            UserEditDialog(
                user = user,
                onSave = { editedUser ->
                    if (editedUser.email.isBlank() || editedUser.name.isBlank()) {
                        errorMsg = "Email and Name required"
                    } else {
                        errorMsg = null
                        viewModel.updateUser(editedUser) // <-- Call your actual ViewModel editing function
                        showEditDialog = null
                    }
                },
                onDismiss = { showEditDialog = null }
            )
        }
        if (showAddDialog) {
            UserEditDialog(
                user = UserModel(id = "", email = "", name = "", role = "User"),
                onSave = { newUser ->
                    if (newUser.email.isBlank() || newUser.name.isBlank()) {
                        errorMsg = "Email and Name required"
                    } else if (users.any { it.email == newUser.email }) {
                        errorMsg = "Duplicate email"
                    } else {
                        errorMsg = null
                        viewModel.addUser(newUser) // <-- Call your actual function to add
                        showAddDialog = false
                    }
                },
                onDismiss = { showAddDialog = false }
            )
        }
        showDeleteDialog?.let { user ->
            DeleteUserDialog(
                user = user,
                onConfirm = {
                    viewModel.deleteUser(user.id) // <-- Call actual ViewModel
                    showDeleteDialog = null
                },
                onDismiss = { showDeleteDialog = null }
            )
        }
    }
}

@Composable
private fun RoleChip(role: String) {
    Surface(
        color = when (role) {
            "Admin" -> Color(0xFFc94b4b)
            "User" -> Color(0xFF4b79a1)
            else -> Color.Gray
        },
        shape = RoundedCornerShape(10.dp),
        contentColor = Color.White,
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Text(role, modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun UserDetailDialog(user: UserModel, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = { TextButton(onClick = onClose) { Text("OK") } },
        title = { Text("User Details") },
        text = {
            Column {
                Text("Name: ${user.name}")
                Text("Email: ${user.email}")
                Text("Phone: ${user.phone}")
                Text("Role: ${user.role}")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserEditDialog(
    user: UserModel,
    onSave: (UserModel) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var role by remember { mutableStateOf(user.role) }
    var phone by remember { mutableStateOf(user.phone) }
    val roles = listOf("User", "Admin")
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onSave(user.copy(name = name, email = email, role = role, phone = phone))
                }
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text(if (user.id.isEmpty()) "Add User" else "Edit User") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = { role = it; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
private fun DeleteUserDialog(
    user: UserModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Delete User") },
        text = { Text("Are you sure you want to delete ${user.email}? This action cannot be undone.") }
    )
}
