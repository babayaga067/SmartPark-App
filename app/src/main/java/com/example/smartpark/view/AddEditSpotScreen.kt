package com.example.smartpark.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartpark.model.ParkingSpotModel
import com.example.smartpark.viewmodel.ParkingSpotViewModel

@Composable
fun AddEditSpotScreen(
    viewModel: ParkingSpotViewModel = viewModel(),
    spotToEdit: ParkingSpotModel? = null,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    var location by remember { mutableStateOf(spotToEdit?.location ?: "") }
    var status by remember { mutableStateOf(spotToEdit?.status ?: "Available") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (spotToEdit != null) "Edit Spot" else "Add Spot", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = status,
            onValueChange = { status = it },
            label = { Text("Status (Available / Reserved)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (location.isBlank()) {
                Toast.makeText(context, "Location required", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val newSpot = ParkingSpotModel(
                id = spotToEdit?.id ?: "",
                location = location,
                status = status
            )

            if (spotToEdit == null) {
                viewModel.addSpot(newSpot) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    if (success) onDone()
                }
            } else {
                viewModel.updateSpot(newSpot) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    if (success) onDone()
                }
            }

        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = if (spotToEdit != null) "Update Spot" else "Add Spot")
        }
    }
}
