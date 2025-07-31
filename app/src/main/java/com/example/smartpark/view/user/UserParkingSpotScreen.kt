package com.example.smartpark.view.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import coil.compose.AsyncImage
import com.example.smartpark.model.ParkingSpotModel
import com.example.smartpark.viewmodel.ParkingSpotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserParkingSpotListScreen(
    viewModel: ParkingSpotViewModel,
    onBookSpot: (ParkingSpotModel) -> Unit,
    onViewSpot: (ParkingSpotModel) -> Unit = {}, // Optional: for viewing spot detail
) {
    val allSpots by viewModel.spots.collectAsState(emptyList())
    var search by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("Available") }
    val statusFilters = listOf("Available", "Reserved", "Booked", "All")
    var isRefreshing by remember { mutableStateOf(false) }

    val filteredSpots = allSpots.filter {
        (statusFilter == "All" || it.status.equals(statusFilter, true))
                && (search.isBlank() || it.name.contains(search, true) || it.location.contains(search, true))
    }.sortedBy { it.name }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE4ECFB), Color(0xFFF7F7FA), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Parking", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier
            .background(backgroundBrush)
            .systemBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search and filter row
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 12.dp, end = 8.dp, top = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                statusFilters.forEach { filter ->
                    val selected = filter.equals(statusFilter, ignoreCase = true)
                    FilterChip(
                        selected = selected,
                        onClick = { statusFilter = filter },
                        label = { Text(filter, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1976D2),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFE3EAFE),
                            labelColor = Color(0xFF1976D2)
                        )
                    )
                    Spacer(Modifier.width(9.dp))
                }
            }
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search by name/location") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { search = "" }) {
                            Icon(Icons.Filled.Close, "Clear Search")
                        }
                    }
                },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 17.dp, vertical = 10.dp)
            )
            Spacer(Modifier.height(2.dp))

            // Spot listing
            if (filteredSpots.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No spots found.", fontWeight = FontWeight.Medium, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    items(filteredSpots, key = { it.id }) { spot ->
                        UserParkingSpotCard(
                            spot = spot,
                            onBook = { onBookSpot(spot) },
                            onView = { onViewSpot(spot) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserParkingSpotCard(
    spot: ParkingSpotModel,
    onBook: () -> Unit,
    onView: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(7.dp, RoundedCornerShape(20.dp))
            .clickable { onView() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            if (spot.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = spot.imageUrl,
                    contentDescription = "Parking Image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3EAFE))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            when (spot.status) {
                                "Available" -> Color(0xFF43cea2).copy(alpha = 0.18f)
                                "Reserved" -> Color(0xFFFDCB6E).copy(alpha = 0.19f)
                                "Booked" -> Color(0xFFE17055).copy(alpha = 0.18f)
                                else -> Color.White.copy(alpha = 0.13f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.LocalParking, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(28.dp))
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(spot.name, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), fontSize = 18.sp)
                Text("Location: ${spot.location}", color = Color.Gray, fontSize = 14.sp)
                AnimatedVisibility(visible = spot.type.isNotEmpty()) {
                    Text("Type: ${spot.type}", color = Color.Gray, fontSize = 13.sp)
                }
                Text("Price: ₹${spot.price}", color = Color(0xFF1976D2), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(spot.status)
                }
            }
            Spacer(Modifier.width(7.dp))
            // Show Book button only if available
            if (spot.status.equals("Available", true)) {
                Button(
                    onClick = onBook,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF43cea2),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(39.dp)
                ) {
                    Icon(Icons.Filled.EventAvailable, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Book", fontWeight = FontWeight.Bold)
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.Block,
                    contentDescription = "Unavailable",
                    tint = Color(0xFFE17055),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (bg, content) = when (status) {
        "Available" -> Color(0xFF43cea2) to Color.White
        "Reserved" -> Color(0xFFFDCB6E) to Color.White
        "Booked" -> Color(0xFFE17055) to Color.White
        else -> Color.LightGray to Color.DarkGray
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp),
        contentColor = content,
        modifier = Modifier.padding(end = 5.dp)
    ) {
        Text(
            status,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}
