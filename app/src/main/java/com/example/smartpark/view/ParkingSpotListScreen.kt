package com.example.smartpark.view
//
//import android.widget.Toast
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.smartpark.model.ParkingSpotModel
//import com.example.smartpark.viewmodel.ParkingSpotViewModel
//
//@Composable
//fun ParkingSpotListScreen(
//    viewModel: ParkingSpotViewModel = viewModel(),
//    onNavigateToAddEdit: (ParkingSpotModel?) -> Unit
//) {
//    val spots by viewModel.spots.observeAsState(initial = emptyList())
//    val isLoading by viewModel.isLoading.observeAsState(initial = false)
//    val context = LocalContext.current
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "Available Parking Spots",
//            style = MaterialTheme.typography.headlineMedium
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = { onNavigateToAddEdit(null) },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Add New Spot")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isLoading) {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//        } else {
//            LazyColumn {
//                items(spots) { spot ->
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp)
//                    ) {
//                        Column(modifier = Modifier.padding(16.dp)) {
//                            Text("Location: ${spot.location}")
//                            Text("Status: ${spot.status}")
//
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.End
//                            ) {
//                                IconButton(onClick = { onNavigateToAddEdit(spot) }) {
//                                    Icon(
//                                        imageVector = Icons.Default.Edit,
//                                        contentDescription = "Edit"
//                                    )
//                                }
//                                IconButton(onClick = {
//                                    viewModel.deleteSpot(spot.id) { success, message ->
//                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//                                    }
//                                }) {
//                                    Icon(
//                                        imageVector = Icons.Default.Delete,
//                                        contentDescription = "Delete"
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}