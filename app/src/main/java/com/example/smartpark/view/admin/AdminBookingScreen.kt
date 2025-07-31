package com.example.smartpark.view.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.smartpark.model.BookingModel
import com.example.smartpark.view.user.toFormattedDateTime
import com.example.smartpark.viewmodel.BookingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingsScreen(
    viewModel: BookingViewModel,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val bookings by viewModel.allBookings.collectAsState(emptyList())
    var search by remember { mutableStateOf(TextFieldValue("")) }
    var statusFilter by remember { mutableStateOf("All") }
    val statusOptions = listOf("All", "Active", "Completed", "Cancelled")
    var sortMode by remember { mutableStateOf("Recent") }
    var sortExpanded by remember { mutableStateOf(false) }
    val sortModes = listOf("Recent", "Oldest", "Highest price", "Lowest price")
    var selectedBooking by remember { mutableStateOf<BookingModel?>(null) }
    val selection by viewModel.selection.collectAsState()
    val toastMsg by viewModel.toastMsg.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val showExportDialog by viewModel.showExportDialog.collectAsState()
    var confirmDeleteDialog by remember { mutableStateOf(false) }

    // Filtering & Sorting
    val filtered =
        bookings
            .filter { statusFilter == "All" || it.status.equals(statusFilter, true) }
            .filter {
                val query = search.text.trim().lowercase()
                query.isEmpty() ||
                        it.spotId.lowercase().contains(query) ||
                        it.userId.lowercase().contains(query) ||
                        it.timestamp.toFormattedDateTime().lowercase().contains(query)
            }
            .let {
                when (sortMode) {
                    "Oldest" -> it.sortedBy { b -> b.timestamp }
                    "Highest price" -> it.sortedByDescending { b -> b.price }
                    "Lowest price" -> it.sortedBy { b -> b.price }
                    else -> it.sortedByDescending { b -> b.timestamp }
                }
            }

    val total = bookings.size
    val totalActive = bookings.count { it.status.equals("Active", true) }
    val totalCompleted = bookings.count { it.status.equals("Completed", true) }
    val totalCancelled = bookings.count { it.status.equals("Cancelled", true) }

    //---Actions---//
    fun refreshAllBookings() = viewModel.refreshAllBookings()

    fun doBatchDelete() {
        val ids = selection.toList()
        var finishedCount = 0
        var successCount = 0
        ids.forEach { id ->
            viewModel.deleteBooking(id) { success, _ ->
                finishedCount++
                if (success) successCount++
                if (finishedCount == ids.size) {
                    viewModel.setToastMsg("Deleted $successCount booking(s)")
                    refreshAllBookings()
                    viewModel.clearSelection()
                }
            }
        }
        confirmDeleteDialog = false
    }

    fun handleBookingStatusChange(booking: BookingModel, newStatus: String) {
        if (booking.status == newStatus) return
        viewModel.updateBooking(booking.copy(status = newStatus)) { success, msg ->
            viewModel.setToastMsg(if (success) "Booking status updated" else "Failed: $msg")
            refreshAllBookings()
        }
    }

    fun exportAsCSV() {
        // Replace with real export logic if needed
        viewModel.setToastMsg("Exported as CSV (demo only)")
        viewModel.setShowExportDialog(false)
    }

    // UI
    Scaffold(
        topBar = {
            Box(
                Modifier.background(
                    Brush.horizontalGradient(listOf(Color(0xFF1976D2), Color(0xFF0073b1)))
                )
            ) {
                TopAppBar(
                    title = { Text("All Bookings", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        navController?.let {
                            IconButton(onClick = { it.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.setShowExportDialog(true) }) {
                            Icon(Icons.Default.Download, contentDescription = "Export CSV", tint = Color.White)
                        }
                        IconButton(onClick = { refreshAllBookings() }) {
                            if (isRefreshing)
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            else
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(visible = selection.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { confirmDeleteDialog = true },
                    icon = { Icon(Icons.Default.Delete, "Batch Delete") },
                    text = { Text("Delete (${selection.size})") },
                    containerColor = Color(0xFFD82342),
                    contentColor = Color.White
                )
            }
        },
        snackbarHost = {
            toastMsg?.let { message ->
                Snackbar(
                    action = { TextButton(onClick = { viewModel.setToastMsg(null) }) { Text("Dismiss") } },
                    modifier = Modifier.padding(14.dp)
                ) { Text(message) }
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier
            .background(Brush.verticalGradient(listOf(Color(0xFFe3f0ff), Color.White)))
            .systemBarsPadding()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter/Sort row
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 15.dp, end = 8.dp, top = 13.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                statusOptions.forEach { option ->
                    FilterChip(
                        selected = option == statusFilter,
                        onClick = { statusFilter = option },
                        label = { Text(option) },
                        leadingIcon = if (statusFilter == option) { { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) } } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1976D2),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFE3EAFE),
                            labelColor = Color(0xFF1976D2)
                        )
                    )
                    Spacer(Modifier.width(9.dp))
                }
                ExposedDropdownMenuBox(expanded = sortExpanded, onExpandedChange = { sortExpanded = it }) {
                    OutlinedTextField(
                        value = sortMode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sort") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(sortExpanded) },
                        modifier = Modifier.menuAnchor().widthIn(min = 96.dp)
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

            // Stats & selection
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 23.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                StatItem("Total", total.toString(), Color(0xFF1976D2))
                StatItem("Active", totalActive.toString(), Color(0xFF56CCF2))
                StatItem("Done", totalCompleted.toString(), Color(0xFF2F80ED))
                StatItem("Cancelled", totalCancelled.toString(), Color(0xFFD82342))
                if (selection.isNotEmpty()) Text("${selection.size} selected", color = Color(0xFFD82342), fontWeight = FontWeight.Bold)
            }

            // Search
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search Spot/User/Date") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    if (search.text.isNotEmpty()) {
                        IconButton(onClick = { search = TextFieldValue("") }) {
                            Icon(Icons.Filled.Close, "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFF99bce2)
                )
            )

            // Result count and error/status
            Text(
                "Showing: ${filtered.size} booking${if (filtered.size == 1) "" else "s"}",
                color = Color(0xFF5A6B81),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 22.dp, bottom = 6.dp, top = 3.dp)
            )

            when {
                isRefreshing && bookings.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                    }
                }
                bookings.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No bookings found.", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
                filtered.isEmpty() && search.text.isNotBlank() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No results found.", color = Color(0xFF2F80ED), fontWeight = FontWeight.SemiBold)
                    }
                }
                else -> {
                    LazyColumn(
                        Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtered, key = { it.id.ifBlank { "${it.spotId}-${it.timestamp}" } }) { booking ->
                            AdminBookingCard(
                                booking = booking,
                                selected = selection.contains(booking.id.ifBlank { booking.spotId }),
                                onSelect = {
                                    val key = booking.id.ifBlank { booking.spotId }
                                    viewModel.toggleSelection(key)
                                },
                                onClick = { selectedBooking = booking },
                                onStatusChange = { newStatus -> handleBookingStatusChange(booking, newStatus) }
                            )
                        }
                    }
                }
            }
        }

        // --- Detail dialog ---
        selectedBooking?.let { booking ->
            AdminBookingDetailDialog(
                booking = booking,
                onDismiss = { selectedBooking = null }
            )
        }

        // --- Export Dialog Demo ---
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setShowExportDialog(false) },
                title = { Text("Export Bookings as CSV") },
                text = { Text("Export all current bookings (according to current filters/search) as a CSV file?") },
                confirmButton = {
                    Button(onClick = { exportAsCSV() }) { Text("Export") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setShowExportDialog(false) }) { Text("Cancel") }
                }
            )
        }

        // --- Confirm Batch Delete Dialog ---
        if (confirmDeleteDialog) {
            AlertDialog(
                onDismissRequest = { confirmDeleteDialog = false },
                title = { Text("Delete Bookings") },
                text = { Text("Are you sure you want to delete all selected bookings? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = { doBatchDelete() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("Delete", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDeleteDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

// ... keep StatItem, AdminBookingCard, StatusChip, AdminBookingDetailDialog as in your current version (see your post).
@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = color, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}
@Composable
private fun AdminBookingCard(
    booking: BookingModel,
    selected: Boolean,
    onSelect: () -> Unit,
    onClick: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    var statusMenuExpanded by remember { mutableStateOf(false) }
    val cardColor = if (selected) Color(0xFFD8EAFF) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (selected) 11.dp else 6.dp, RoundedCornerShape(16.dp))
            .background(cardColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onSelect,
            ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Spot: ${booking.spotId}", fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F80ED), fontSize = 17.sp
                )
                Text("User: ${booking.userId}", fontWeight = FontWeight.Medium, color = Color(0xFF7B8FA7), fontSize = 15.sp)
                Text(
                    "Time: ${booking.timestamp.toFormattedDateTime()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1976D2)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Box {
                    StatusChip(booking.status, onClick = { statusMenuExpanded = true })
                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        if (booking.status != "Active")
                            DropdownMenuItem(
                                text = { Text("Mark as Active") },
                                onClick = { statusMenuExpanded = false; onStatusChange("Active") }
                            )
                        if (booking.status != "Completed")
                            DropdownMenuItem(
                                text = { Text("Mark as Completed") },
                                onClick = { statusMenuExpanded = false; onStatusChange("Completed") }
                            )
                        if (booking.status != "Cancelled")
                            DropdownMenuItem(
                                text = { Text("Mark as Cancelled") },
                                onClick = { statusMenuExpanded = false; onStatusChange("Cancelled") }
                            )
                    }
                }
                Spacer(Modifier.height(9.dp))
                Text(
                    "₹${booking.price}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F80ED),
                    style = MaterialTheme.typography.titleMedium
                )
                Checkbox(checked = selected, onCheckedChange = { onSelect() })
            }
        }
    }
}
@Composable
private fun StatusChip(status: String, onClick: (() -> Unit)? = null) {
    val (labelColor, bgColor) = when (status) {
        "Active" -> Color(0xFF1976D2) to Color(0xFFD8EAFF)
        "Completed" -> Color(0xFF46C378) to Color(0xFFDCF5DF)
        "Cancelled" -> Color(0xFFD82342) to Color(0xFFFFF0F2)
        else -> Color.DarkGray to Color(0xFFE0E0E0)
    }
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(11.dp))
            .run { if (onClick != null) clickable { onClick() } else this },
        color = bgColor,
        contentColor = labelColor
    ) {
        Text(
            status,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.5.dp)
        )
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
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.widthIn(min = 260.dp, max = 370.dp)
            ) {
                Row { Text("Spot:", fontWeight = FontWeight.Bold); Spacer(Modifier.width(8.dp)); Text(booking.spotId) }
                Row { Text("User ID:", fontWeight = FontWeight.Bold); Spacer(Modifier.width(8.dp)); Text(booking.userId) }
                Row { Text("Date/Time:", fontWeight = FontWeight.Bold); Spacer(Modifier.width(8.dp)); Text(booking.timestamp.toFormattedDateTime()) }
                Row { Text("Price:", fontWeight = FontWeight.Bold); Spacer(Modifier.width(8.dp)); Text("₹${booking.price}") }
                Row { Text("Status:", fontWeight = FontWeight.Bold); Spacer(Modifier.width(8.dp)); StatusChip(booking.status) }
            }
        }
    )
}


