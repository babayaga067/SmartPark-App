package com.example.smartpark.view.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartpark.model.ParkingSpotModel
import com.example.smartpark.viewmodel.BookingViewModel
import com.example.smartpark.viewmodel.ParkingSpotViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBookingScreen(
    navController: NavController,
    spot: ParkingSpotModel,
    bookingViewModel: BookingViewModel,
    parkingSpotViewModel: ParkingSpotViewModel
) {
    val context = LocalContext.current
    val isBusy by bookingViewModel.isBusy.collectAsState()
    val errorMsg by bookingViewModel.errorMsg.collectAsState()
    val bookingDate = remember { Date() }
    val bookingDateString = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(bookingDate)
    }

    fun makeBooking() {
        // Ensure spot is still available at click time
        if (spot.status != "Available") {
            bookingViewModel.setErrorMsg("This spot is no longer available.")
            return
        }
        bookingViewModel.makeBooking(spot, parkingSpotViewModel, navController, bookingDateString)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFE6EFFF), Color.White))
            )
            .padding(horizontal = 20.dp)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.widthIn(max = 460.dp),
            elevation = CardDefaults.cardElevation(15.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Spot Image (if present)
                if (spot.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = spot.imageUrl,
                        contentDescription = "Spot Image",
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Icon(
                    Icons.Filled.LocalParking, null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(46.dp)
                )
                Text("Confirm Your Booking",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(vertical = 7.dp)
                )
                Divider(Modifier.padding(vertical = 6.dp))

                BookingDetailRow("Spot Name", spot.name)
                BookingDetailRow("Location", spot.location)
                BookingDetailRow("Price", "₹${spot.price}")
                BookingDetailRow("Booking Date", bookingDateString)
                BookingDetailRow("Status", spot.status)

                Spacer(Modifier.height(16.dp))
                errorMsg?.let {
                    Text(it, color = Color.Red, modifier = Modifier.padding(bottom = 6.dp))
                }

                Button(
                    onClick = { makeBooking() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    enabled = spot.status == "Available" && !isBusy
                ) {
                    if (isBusy) CircularProgressIndicator(Modifier.size(19.dp), color = Color.White)
                    else Text("Book Spot", fontWeight = FontWeight.Bold)
                }
                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(top = 7.dp)
                ) {
                    Text("Back", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun BookingDetailRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Color(0xFF1976D2))
        Text(value, fontWeight = FontWeight.Medium, color = Color(0xFF222B45))
    }
}
