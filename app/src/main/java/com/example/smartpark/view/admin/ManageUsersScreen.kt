package com.example.smartpark.view.admin

import androidx.compose.animation.AnimatedVisibility
import com.example.smartpark.model.UserModel
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartpark.viewmodel.adminViewModel.ManageUsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    viewModel: ManageUsersViewModel,
    navController: NavController
) {
    val users by viewModel.users.collectAsState(emptyList())
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var search by remember { mutableStateOf("") }
    var roleFilter by remember { mutableStateOf("All") }
    var sortMode by remember { mutableStateOf("Recent") }
    var sortExpanded by remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showEditDialog by remember { mutableStateOf<UserModel?>(null) }
    var showViewDialog by remember { mutableStateOf<UserModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf<UserModel?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showResetPwdDialog by remember { mutableStateOf<UserModel?>(null) }
    var toastMsg by remember { mutableStateOf<String?>(null) }
    var localErrorMsg by remember { mutableStateOf<String?>(null) }

    val roles = listOf("All", "User", "Admin")
    val sortModes = listOf("Recent", "A-Z", "Admins First", "Users First")

    // Filtering, Sorting
    val filtered = users
        .filter { roleFilter == "All" || it.role.equals(roleFilter, true) }
        .filter { search.isBlank() || it.email.contains(search, true) || it.name.contains(search, true) }
        .let {
            when (sortMode) {
                "A-Z" -> it.sortedBy { u -> u.name }
                "Admins First" -> it.sortedByDescending { u -> u.role }
                "Users First" -> it.sortedBy { u -> u.role }
                else -> it.asReversed()
            }
        }

    val totalAdmins = users.count { it.role.equals("Admin", true) }
    val totalUsers = users.count { it.role.equals("User", true) }

    // Theme gradient background
    val backgroundBrush = Brush.verticalGradient(
        listOf(Color(0xFFF8FAFF), Color(0xFFF0F3FA), Color(0xFFFFFFFF))
    )

    // On enter, always refresh user list!
    LaunchedEffect(true) {
        viewModel.fetchUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Users", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2)),
                actions = {
                    if (selection.isNotEmpty()) {
                        IconButton(onClick = { selection = emptySet() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Selection", tint = Color.White)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF1976D2)
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = "Add User", tint = Color.White)
            }
        },
        snackbarHost = {
            toastMsg?.let { message ->
                Snackbar(
                    action = { TextButton(onClick = { toastMsg = null }) { Text("Dismiss") } }
                ) { Text(message) }
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .systemBarsPadding()
                .padding(innerPadding)
        ) {
            // FILTER and SORT BAR
            Row(
                Modifier
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Search") },
                    singleLine = true,
                    modifier = Modifier.width(168.dp).heightIn(min = 50.dp),
                    shape = RoundedCornerShape(13.dp)
                )
                Spacer(Modifier.width(8.dp))
                roles.forEach { role ->
                    FilterChip(
                        selected = roleFilter == role,
                        onClick = { roleFilter = role },
                        label = { Text(role) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1976D2),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFEDEEF7),
                            labelColor = Color(0xFF1976D2)
                        )
                    )
                    Spacer(Modifier.width(7.dp))
                }
                ExposedDropdownMenuBox(
                    expanded = sortExpanded,
                    onExpandedChange = { sortExpanded = it }
                ) {
                    OutlinedTextField(
                        value = sortMode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sort") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(sortExpanded) },
                        modifier = Modifier.menuAnchor().widthIn(min = 98.dp).heightIn(min = 50.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false }
                    ) {
                        sortModes.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = { sortMode = it; sortExpanded = false }
                            )
                        }
                    }
                }
            }

            // Stats Row
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, bottom = 2.dp, top = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(17.dp)
            ) {
                Text("Total: ${users.size}", color = Color(0xFF1976D2), fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Text("Admins: $totalAdmins", color = Color(0xFF2F80ED), fontWeight = FontWeight.Bold)
                Text("Users: $totalUsers", color = Color(0xFF43cea2), fontWeight = FontWeight.Bold)
                if (selection.isNotEmpty())
                    Text("${selection.size} selected", color = Color(0xFFD82342), fontWeight = FontWeight.Bold)
            }

            // Bulk Action Bar
            AnimatedVisibility(visible = selection.isNotEmpty()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF4FAFF))
                        .padding(horizontal = 17.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${selection.size} selected")
                    Row {
                        IconButton(
                            onClick = {
                                selection.forEach { id -> viewModel.deleteUser(id) { success, msg ->
                                    toastMsg = msg
                                } }
                                selection = emptySet()
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                        IconButton(
                            onClick = {
                                // Promote all to admin
                                selection.forEach { id ->
                                    users.find { it.id == id }?.let {
                                        viewModel.updateUser(it.copy(role = "Admin")) { success, msg ->
                                            toastMsg = msg
                                        }
                                    }
                                }
                                selection = emptySet()
                            }
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Promote", tint = Color(0xFF1976D2))
                        }
                        IconButton(onClick = { selection = emptySet() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                }
            }

            error?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(start = 18.dp, bottom = 4.dp))
            }
            localErrorMsg?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(start = 18.dp, bottom = 4.dp))
            }

            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                }
                users.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users found.", color = Color(0xFF2F80ED), fontWeight = FontWeight.Medium)
                }
                else -> LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { user ->
                        UserAdminCard(
                            user = user,
                            selected = selection.contains(user.id),
                            onSelect = {
                                selection = if (selection.contains(user.id))
                                    selection - user.id
                                else selection + user.id
                            },
                            onView = { showViewDialog = user },
                            onEdit = { showEditDialog = user },
                            onDelete = { showDeleteDialog = user },
                            onResetPwd = { showResetPwdDialog = user },
                            onCardClick = { showViewDialog = user }
                        )
                    }
                }
            }
        }

        // --- Dialogs ---
        showViewDialog?.let { user ->
            UserDetailDialog(
                user = user,
                onClose = { showViewDialog = null },
                onResetPwd = { showResetPwdDialog = user }
            )
        }
        showEditDialog?.let { user ->
            UserEditDialog(
                user = user,
                onSave = { editedUser ->
                    if (editedUser.email.isBlank() || editedUser.name.isBlank()) {
                        localErrorMsg = "Email and Name required"
                    } else {
                        localErrorMsg = null
                        viewModel.updateUser(editedUser) { success, msg -> toastMsg = msg }
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
                        localErrorMsg = "Email and Name required"
                    } else if (users.any { it.email == newUser.email }) {
                        localErrorMsg = "Duplicate email"
                    } else {
                        localErrorMsg = null
                        viewModel.addUser(newUser) { success, msg -> toastMsg = msg }
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
                    viewModel.deleteUser(user.id) { success, msg -> toastMsg = msg }
                    showDeleteDialog = null
                    selection = selection - user.id
                },
                onDismiss = { showDeleteDialog = null }
            )
        }
        showResetPwdDialog?.let { user ->
            ResetPasswordDialog(
                user = user,
                onConfirm = {
                    viewModel.sendResetPassword(user.email)
                    showResetPwdDialog = null
                    toastMsg = "Password reset email sent"
                },
                onDismiss = { showResetPwdDialog = null }
            )
        }
    }
}

@Composable
private fun UserAdminCard(
    user: UserModel,
    selected: Boolean,
    onSelect: () -> Unit,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onResetPwd: () -> Unit,
    onCardClick: () -> Unit
) {
    val selectedColor = if (selected) Color(0xFFD8EAFF) else Color.White
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(7.dp, RoundedCornerShape(19.dp))
            .background(selectedColor)
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = onSelect
            ),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = selectedColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(15.dp)
        ) {
            if (user.profileImageUrl.isNotBlank()) {
                AsyncImage(
                    model = user.profileImageUrl,
                    contentDescription = "User Image",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3EAFE))
                )
            } else {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (user.role == "Admin") Color(0xFF1976D2)
                            else Color(0xFF4b79a1).copy(alpha = 0.19f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user.name.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    user.email,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    style = MaterialTheme.typography.titleMedium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Role: ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    RoleChip(user.role)
                }
                Text("Name: ${user.name}", color = Color.Gray, fontSize = 13.sp)
                if (user.phone.isNotBlank())
                    Text("Phone: ${user.phone}", color = Color(0xFF2F80ED), fontSize = 13.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onView) {
                    Icon(Icons.Filled.Info, "View", tint = Color(0xFF4C9AFC))
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, "Edit", tint = Color(0xFF2F80ED))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, "Delete", tint = Color.Red)
                }
                IconButton(onClick = onResetPwd) {
                    Icon(Icons.Filled.VpnKey, "Reset Password", tint = Color(0xFF43cea2))
                }
                Checkbox(
                    checked = selected,
                    onCheckedChange = { onSelect() },
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun RoleChip(role: String) {
    Surface(
        color = when (role) {
            "Admin" -> Color(0xFF1976D2)
            "User" -> Color(0xFF43cea2)
            else -> Color.Gray
        },
        shape = RoundedCornerShape(10.dp),
        contentColor = Color.White,
        modifier = Modifier.padding(start = 6.dp)
    ) {
        Text(role, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun UserDetailDialog(user: UserModel, onClose: () -> Unit, onResetPwd: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Row {
                TextButton(onClick = onResetPwd) { Text("Reset Password") }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onClose) { Text("Close") }
            }
        },
        title = { Text("User Details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Name: ${user.name}", fontWeight = FontWeight.Bold)
                Text("Email: ${user.email}")
                if (user.phone.isNotBlank()) Text("Phone: ${user.phone}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Role: ", fontWeight = FontWeight.Medium)
                    RoleChip(user.role)
                }
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
                onClick = { onSave(user.copy(name = name, email = email, role = role, phone = phone)) }
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text(if (user.id.isEmpty()) "Add User" else "Edit User") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth()
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
                    label = { Text("Phone") }, singleLine = true, modifier = Modifier.fillMaxWidth()
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
        text = { Text("Are you sure you want to delete user '${user.email}'? This cannot be undone.") }
    )
}

@Composable
private fun ResetPasswordDialog(
    user: UserModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Send Email")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Reset Password") },
        text = { Text("Send a reset password email to: ${user.email}?") }
    )
}
