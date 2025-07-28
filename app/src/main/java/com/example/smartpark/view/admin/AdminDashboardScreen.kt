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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val adminEmail by viewModel.adminEmail.collectAsState()
    val revenue by viewModel.totalRevenue.collectAsState()
    val lastRefreshed = remember { mutableLongStateOf(System.currentTimeMillis()) }
    var statInfoDialog by remember { mutableStateOf<StatInfo?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAdminInfo()
        viewModel.fetchTotalRevenue()
        lastRefreshed.longValue = System.currentTimeMillis()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFa8e063), Color(0xFF56ab2f))))
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Profile Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = 7.dp,
                    color = Color.White
                ) {
                    Row(
                        Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF388E3C).copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFF388E3C),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Admin",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF185a9d),
                                fontWeight = FontWeight.ExtraBold
                            )
                            adminEmail?.let {
                                Text(
                                    it,
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Revenue Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    shadowElevation = 5.dp,
                    color = Color.White
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF43cea2), Color(0xFF56ab2f))
                                )
                            )
                            .padding(15.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Payments,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Total Revenue Collected",
                                    color = Color.White.copy(alpha = 0.88f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    "₹$revenue",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Reporting refreshed: ${
                                SimpleDateFormat("hh:mm a", Locale.getDefault())
                                    .format(Date(lastRefreshed.longValue))
                            }",
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Stats Badges Row (with Material3 ripple by default, no manual indication needed)
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBadge(
                        label = "Booked",
                        value = "12",
                        color = Color(0xFFE17055),
                        icon = Icons.Filled.LocalParking
                    ) { statInfoDialog = StatInfo("Booked", "Number of parking spots currently booked by users.") }

                    StatBadge(
                        label = "Available",
                        value = "8",
                        color = Color(0xFF43cea2),
                        icon = Icons.Filled.LocalParking
                    ) { statInfoDialog = StatInfo("Available", "Number of parking spots available to be booked right now.") }

                    StatBadge(
                        label = "Users",
                        value = "143",
                        color = Color(0xFF185a9d),
                        icon = Icons.Filled.Person
                    ) { statInfoDialog = StatInfo("Users", "Total registered users (admin + normal).") }
                }
            }

            // Divider & Title
            item {
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color(0xFFb2d8b8)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Admin Management",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF185a9d),
                    modifier = Modifier.padding(top = 5.dp)
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
                                navController.navigate("login") { popUpTo("adminDashboard") { inclusive = true } }
                            }
                            else -> navController.navigate(action.route)
                        }
                    }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }

        // Stat info dialog shown when user taps a StatBadge
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

private data class AdminAction(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val color: Color,
    val route: String,
    val isDanger: Boolean = false
)

private val adminActionCards = listOf(
    AdminAction("Manage Parking Spots", "Add, edit, remove, or review all parking locations.",
        Icons.Filled.Edit, Color(0xFF43cea2), "parkingSpots"),
    AdminAction("View All Bookings", "See all bookings, check timeslots, and manage occupancy.",
        Icons.AutoMirrored.Filled.List, Color(0xFF56ab2f), "adminBookings"),
    AdminAction("Manage Users", "View and manage all registered users.",
        Icons.Filled.Person, Color(0xFF185a9d), "manageUsers"),
    AdminAction("Logout", "Sign out of admin panel.",
        Icons.AutoMirrored.Filled.ExitToApp, Color.Red, "logout", isDanger = true)
)

@Composable
private fun StatBadge(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    // Use just clickable - Material3 provides correct ripple, no need to specify
    Surface(
        color = color, // Opaque and prominent
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(6.dp))
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
            .heightIn(min = 54.dp, max = 60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = bgColor,
                modifier = Modifier.size(29.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = if (isDanger) Color.Red else bgColor,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    description,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}
