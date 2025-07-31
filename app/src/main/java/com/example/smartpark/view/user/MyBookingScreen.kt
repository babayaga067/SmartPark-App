package com.example.smartpark.view.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartpark.model.BookingModel
import com.example.smartpark.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    navController: NavController,
    viewModel: BookingViewModel = viewModel()
) {
    val myBookings by viewModel.userBookings.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadUserBookings() }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF8FAFF), Color(0xFFF0F3FA), Color(0xFFFFFFFF)
        ),
        startY = 0f, endY = 1500f
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Bookings",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack("userDashboard", inclusive = false)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 8.dp, start = 12.dp, end = 12.dp), // Less vertical padding (AppBar is used)
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // No need for extra heading ("My Bookings") here, handled by TopAppBar

                if (myBookings.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No bookings yet.\nYour reserved spots will show here.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 22.dp)
                    ) {
                        items(myBookings.sortedByDescending { it.timestamp }) { booking ->
                            BookingCard(booking = booking)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingCard(booking: BookingModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(7.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 18.dp, horizontal = 15.dp)
        ) {
            // Left: circle blue icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1976D2).copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.LocalParking,
                    contentDescription = "Booked Spot",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(13.dp))
            // Main booking details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Spot: ${booking.spotId}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Booked on: ${booking.timestamp.toFormattedDateTime()}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Price: ₹${booking.price}",
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                BookingStatusLabel(booking.status)
            }
        }
    }
}

@Composable
private fun BookingStatusLabel(status: String) {
    val (labelColor, bgColor) = when (status) {
        "Active" -> Color(0xFF1976D2) to Color(0xFFD8EAFF)
        "Completed" -> Color(0xFF46C378) to Color(0xFFDCF5DF)
        "Cancelled" -> Color(0xFFD82342) to Color(0xFFFFF0F2)
        else -> Color.DarkGray to Color(0xFFE0E0E0)
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(
            status,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

fun Long.toFormattedDateTime(): String =
    try {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(this))
    } catch (_: Exception) {
        this.toString()
    }
