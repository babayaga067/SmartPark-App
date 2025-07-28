package com.example.smartpark.view.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartpark.model.BookingModel
import com.example.smartpark.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingsScreen(
    viewModel: BookingViewModel,
    navController: NavController? = null
) {
    // State observation
    val bookings by viewModel.allBookings.collectAsState(emptyList())
    var search by remember { mutableStateOf(TextFieldValue("")) }
    var selectedBooking by remember { mutableStateOf<BookingModel?>(null) }

    val filtered = bookings
        .asReversed() // most recent first
        .filter {
            val query = search.text.trim().lowercase()
            if (query.isEmpty()) true
            else it.spotId.lowercase().contains(query)
                    || it.userId.lowercase().contains(query)
                    || it.timestamp.toFormattedDateTime().lowercase().contains(query)
        }

    // Scaffold with TopAppBar
    Scaffold(
        topBar = {
            // Layer for gradient (simulate gradient bar with Box)
            Box(
                Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF56CCF2), Color(0xFF2F80ED))
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Text("All Bookings", fontWeight = FontWeight.Bold, color = Color.White)
                    },
                    navigationIcon = {
                        navController?.let {
                            IconButton(onClick = { it.popBackStack() }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent // still get gradient from Box
                    )
                )
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier
            .background(Brush.verticalGradient(listOf(Color(0xFF56CCF2), Color(0xFF2F80ED))))
            .systemBarsPadding()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search by Spot, User, or Date") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (search.text.isNotEmpty()) {
                        IconButton(onClick = { search = TextFieldValue("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear Search")
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                "Showing: ${filtered.size} booking${if (filtered.size == 1) "" else "s"}",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 22.dp, bottom = 6.dp)
            )

            when {
                bookings.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                filtered.isEmpty() && search.text.isNotBlank() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No results found.", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
                else -> {
                    // Use key = { it.bookingId } if you have one
                    LazyColumn(
                        Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filtered) { booking ->
                            AdminBookingCard(
                                booking = booking,
                                onClick = { selectedBooking = booking }
                            )
                        }
                    }
                }
            }
        }

        // --- Booking Details Dialog ---
        selectedBooking?.let { booking ->
            AdminBookingDetailDialog(
                booking = booking,
                onDismiss = { selectedBooking = null }
            )
        }
    }
}

@Composable
private fun AdminBookingCard(
    booking: BookingModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Row(Modifier.padding(vertical = 15.dp, horizontal = 14.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Spot: ${booking.spotId}", fontWeight = FontWeight.Bold, color = Color(0xFF2F80ED))
                Text("User: ${booking.userId}", fontWeight = FontWeight.Medium, color = Color(0xFF7B8FA7))
                Text(
                    "Time: ${booking.timestamp.toFormattedDateTime()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2F80ED)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "₹${booking.price}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F80ED),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun AdminBookingDetailDialog(
    booking: BookingModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        title = { Text("Booking Details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row { Text("Spot:", fontWeight = FontWeight.Bold); Spacer(Modifier.width(6.dp)); Text(booking.spotId) }
                Row { Text("User ID:", fontWeight = FontWeight.Bold); Spacer(Modifier.width(6.dp)); Text(booking.userId) }
                Row { Text("Date/Time:", fontWeight = FontWeight.Bold); Spacer(Modifier.width(6.dp)); Text(booking.timestamp.toFormattedDateTime()) }
                Row { Text("Price:", fontWeight = FontWeight.Bold); Spacer(Modifier.width(6.dp)); Text("₹${booking.price}") }
            }
        }
    )
}

fun Long.toFormattedDateTime(): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(this))
