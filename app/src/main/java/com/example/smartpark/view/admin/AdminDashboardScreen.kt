package com.example.smartpark.view.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.smartpark.viewmodel.adminViewModel.AdminDashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminDashboardViewModel
) {
    val context = LocalContext.current

    // Collect Real-time State
    val adminEmail by viewModel.adminEmail.collectAsState()
    val revenue by viewModel.totalRevenue.collectAsState()
    val totalBookings by viewModel.totalBookings.collectAsState()
    val availableSpots by viewModel.availableSpots.collectAsState()
    val totalUsers by viewModel.totalUsers.collectAsState()
    val pendingActions by viewModel.pendingActions.collectAsState()
    val recentBookings by viewModel.recentBookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    val lastRefreshed = viewModel.lastRefreshed.collectAsState(initial = System.currentTimeMillis())

    var statInfoDialog by remember { mutableStateOf<StatInfo?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAdminInfo()
        viewModel.loadDashboardStats()
    }

    val backgroundBrush = Brush.verticalGradient(
        listOf(Color(0xFFF8FAFF), Color(0xFFF0F3FA), Color(0xFFFFFFFF)),
        startY = 0f, endY = 1400f
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartPark Admin", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2)),
                actions = {
                    BadgedBox(
                        badge = {
                            if (pendingActions > 0) {
                                Badge(containerColor = Color(0xFFD82342)) {
                                    Text("$pendingActions", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = {
                            navController.navigate("adminActions")
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Pending")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Parking Spot") },
                icon = { Icon(Icons.Default.AddLocation, contentDescription = null) },
                onClick = { navController.navigate("addEditParkingSpot") },
                shape = RoundedCornerShape(22.dp),
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                }
            } else {
                errorMsg?.let {
                    Snackbar(
                        action = { TextButton(onClick = { viewModel.clearError() }) { Text("Dismiss") } }
                    ) { Text(it) }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Profile Card
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(10.dp, RoundedCornerShape(32.dp)),
                            shape = RoundedCornerShape(32.dp),
                            color = Color.White
                        ) {
                            Row(
                                Modifier
                                    .padding(vertical = 20.dp, horizontal = 17.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1976D2).copy(alpha = 0.13f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = Color(0xFF1976D2),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "Admin Dashboard",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF1976D2)
                                    )
                                    adminEmail?.let {
                                        Text(
                                            it,
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Text(
                                        "ID: #ADM-001",
                                        color = Color(0xFF1976D2).copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // Revenue Card
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 92.dp)
                                .shadow(7.dp, RoundedCornerShape(26.dp)),
                            shape = RoundedCornerShape(26.dp),
                            color = Color.White
                        ) {
                            Row(
                                Modifier
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF4481EB), Color(0xFF2F80ED))
                                        )
                                    )
                                    .padding(start = 19.dp, top = 19.dp, end = 22.dp, bottom = 18.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Payments,
                                    tint = Color.White,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        "Total Revenue",
                                        color = Color.White.copy(alpha = 0.90f),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        "₹${revenue ?: "--"}",
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        "Refreshed: ${
                                            SimpleDateFormat("hh:mm a", Locale.getDefault())
                                                .format(Date(lastRefreshed.value))
                                        }",
                                        color = Color.White.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Stats Row
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBadge(
                                label = "Booked",
                                value = "${totalBookings ?: "--"}",
                                color = Color(0xFFE17055),
                                icon = Icons.Filled.LocalParking
                            ) { statInfoDialog = StatInfo("Booked", "Total parking spots currently booked by users.") }

                            StatBadge(
                                label = "Available",
                                value = "${availableSpots ?: "--"}",
                                color = Color(0xFF4C9AFC),
                                icon = Icons.Filled.EventAvailable
                            ) { statInfoDialog = StatInfo("Available", "Spots available right now for booking.") }

                            StatBadge(
                                label = "Users",
                                value = "${totalUsers ?: "--"}",
                                color = Color(0xFF1976D2),
                                icon = Icons.Filled.PeopleAlt
                            ) { statInfoDialog = StatInfo("Users", "Total registered users including admins.") }
                        }
                    }

                    // Recent Bookings Preview
                    item {
                        Text(
                            "Recent Bookings",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1976D2),
                            modifier = Modifier.padding(bottom = 4.dp, start = 6.dp)
                        )
                        Surface(
                            shape = RoundedCornerShape(17.dp),
                            color = Color.White,
                            shadowElevation = 6.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(vertical = 8.dp)) {
                                if (recentBookings.isEmpty()) {
                                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Text("No recent bookings.", color = Color(0xFF1976D2))
                                    }
                                } else {
                                    recentBookings.forEach { booking ->
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 13.dp, vertical = 8.dp)
                                                .clickable {
                                                    navController.navigate("bookingDetail/${booking.id}")
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                Modifier.size(38.dp).clip(CircleShape)
                                                    .background(Color(0xFF1976D2).copy(alpha = 0.16f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(booking.name.take(1).uppercase(), color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(Modifier.width(13.dp))
                                            Column(Modifier.weight(1f)) {
                                                Text(
                                                    "${booking.name} • ${booking.plate}",
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1976D2),
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontSize = 16.sp
                                                )
                                                Text(
                                                    booking.date,
                                                    color = Color.Gray,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            BookingStatusChip(booking.status)
                                        }
                                        if (booking != recentBookings.last()) HorizontalDivider(
                                            Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                                            color = Color(0xFFEDF3FF), thickness = 1.dp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Divider & Section Label
                    item {
                        Spacer(Modifier.height(3.dp))
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color(0xFFE3EAFE)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Admin Management",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1976D2),
                            modifier = Modifier.padding(start = 6.dp, top = 6.dp)
                        )
                    }

                    // Admin Management Action Cards
                    items(adminActionCards) { action ->
                        AdminActionCard(
                            title = action.title,
                            description = action.desc,
                            icon = action.icon,
                            bgColor = action.color,
                            isDanger = action.isDanger,
                            onClick = {
                                when (action.route) {
                                    "logout" -> {
                                        viewModel.logout()
                                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                                        navController.navigate("login") {
                                            popUpTo("adminDashboard") { inclusive = true }
                                        }
                                    }
                                    else -> navController.navigate(action.route)
                                }
                            }
                        )
                    }
                }
            }
        }

        statInfoDialog?.let {
            AlertDialog(
                onDismissRequest = { statInfoDialog = null },
                confirmButton = {
                    TextButton(onClick = { statInfoDialog = null }) { Text("OK") }
                },
                title = { Text(it.title) },
                text = { Text(it.desc) }
            )
        }
    }
}

private data class StatInfo(val title: String, val desc: String)

// You should fetch RecentBooking from backend, here just a sample:
data class RecentBooking(
    val id: String, // Add id for navigation!
    val name: String,
    val plate: String,
    val status: String,
    val date: String
)

private data class AdminAction(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val color: Color,
    val route: String,
    val isDanger: Boolean = false
)

private val adminActionCards = listOf(
    AdminAction("Manage Parking Spots", "Add, edit, remove or review all parking locations.",
        Icons.Filled.Edit, Color(0xFF4C9AFC), "parkingSpots"),
    AdminAction("View All Bookings", "See all bookings, check timeslots and manage occupancy.",
        Icons.AutoMirrored.Filled.List, Color(0xFF2F80ED), "adminBookings"),
    AdminAction("Manage Users", "View and manage all registered users.",
        Icons.Filled.Person, Color(0xFF1976D2), "manageUsers"),
    AdminAction("Logout", "Sign out of admin panel.",
        Icons.AutoMirrored.Filled.ExitToApp, Color.Red, "logout", isDanger = true)
)

@Composable
private fun BookingStatusChip(status: String) {
    val (labelColor, bgColor) = when (status) {
        "Active" -> Color(0xFF1976D2) to Color(0xFFD8EAFF)
        "Completed" -> Color(0xFF46C378) to Color(0xFFDCF5DF)
        "Cancelled" -> Color(0xFFD82342) to Color(0xFFFFF0F2)
        else -> Color.DarkGray to Color(0xFFE0E0E0)
    }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        tonalElevation = 1.dp,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            status, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = labelColor,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun StatBadge(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        color = color,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(21.dp))
            Spacer(Modifier.width(7.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.93f))
                Text(value, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    bgColor: Color,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 65.dp, max = 80.dp)
            .padding(vertical = 1.dp),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = bgColor,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = if (isDanger) Color.Red else bgColor,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    description,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}
