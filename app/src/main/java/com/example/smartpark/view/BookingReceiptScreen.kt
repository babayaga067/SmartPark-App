package com.example.smartpark.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingReceiptScreen(
    spotName: String,
    date: String,
    price: Int,
    onBack: (() -> Unit)? = null // Optional: back/cancel handler
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFa8edea), Color(0xFFfed6e3))
                )
            )
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Booking Confirmed!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF185a9d),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                Spacer(Modifier.height(16.dp))

                Text("Parking Spot", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                Text(
                    spotName, style = MaterialTheme.typography.titleLarge, color = Color.Black, fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(14.dp))

                Text("Booking Date", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                Text(date, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF185a9d))
                Spacer(Modifier.height(14.dp))

                Text("Amount Paid", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                Text("₹$price",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Bold
                )

                // Optional: Add a QR code or digital receipt visual here
                Spacer(Modifier.height(18.dp))
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                Spacer(Modifier.height(12.dp))

                Text(
                    "Show this receipt at the gate or present on request.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(18.dp))

                if (onBack != null) {
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) { Text("Back to Home") }
                }
            }
        }
    }
}
