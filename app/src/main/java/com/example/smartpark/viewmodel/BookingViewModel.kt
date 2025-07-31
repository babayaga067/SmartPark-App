package com.example.smartpark.viewmodel

import androidx.lifecycle.ViewModel
import com.example.smartpark.model.BookingModel
import com.example.smartpark.model.ParkingSpotModel
import com.example.smartpark.repository.AuthRepository
import com.example.smartpark.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BookingViewModel(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    // User and Admin views
    private val _userBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val userBookings: StateFlow<List<BookingModel>> = _userBookings

    private val _allBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val allBookings: StateFlow<List<BookingModel>> = _allBookings

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg

    val selection = MutableStateFlow<Set<String>>(emptySet())
    val toastMsg = MutableStateFlow<String?>(null)
    val showExportDialog = MutableStateFlow(false)
    val isRefreshing = MutableStateFlow(false)

    fun loadUserBookings() {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        _isBusy.value = true
        bookingRepository.getBookingsForUser(userId) { bookings ->
            _userBookings.value = bookings
            _isBusy.value = false
        }
    }

    fun loadAllBookings(onDone: (() -> Unit)? = null) {
        _isBusy.value = true
        bookingRepository.getAllBookings { bookings ->
            _allBookings.value = bookings
            _isBusy.value = false
            onDone?.invoke()
        }
    }

    fun refreshAllBookings() {
        isRefreshing.value = true
        loadAllBookings { isRefreshing.value = false }
    }

    fun makeBooking(
        spot: ParkingSpotModel,
        parkingSpotViewModel: Any, // (type optional or replace as needed)
        navController: androidx.navigation.NavController,
        bookingDateString: String
    ) {
        val userId = authRepository.getCurrentUser()?.uid.orEmpty()
        if (userId.isBlank()) {
            setErrorMsg("User not logged in")
            return
        }
        _isBusy.value = true
        _errorMsg.value = null
        val booking = BookingModel(
            id = "",
            userId = userId,
            spotId = spot.id,
            spotName = spot.name,
            price = spot.price,
            timestamp = System.currentTimeMillis(),
            status = "Active"
        )
        bookingRepository.addBooking(booking) { success, msg ->
            _isBusy.value = false
            if (success) {
                loadUserBookings()
                navController.popBackStack()
            } else {
                setErrorMsg(msg)
            }
        }
    }

    fun updateBooking(booking: BookingModel, callback: (Boolean, String) -> Unit) {
        _isBusy.value = true
        bookingRepository.updateBooking(booking) { success, msg ->
            _isBusy.value = false
            if (success) loadAllBookings()
            callback(success, msg)
        }
    }

    fun deleteBooking(id: String, callback: (Boolean, String) -> Unit) {
        _isBusy.value = true
        bookingRepository.deleteBooking(id) { success, msg ->
            _isBusy.value = false
            if (success) loadAllBookings()
            callback(success, msg)
        }
    }

    // ADMIN BATCH delete
    fun batchDeleteSelected() {
        val ids = selection.value.toList()
        var finishedCount = 0
        var successCount = 0
        ids.forEach { id ->
            deleteBooking(id) { success, _ ->
                finishedCount++
                if (success) successCount++
                if (finishedCount == ids.size) {
                    setToastMsg("Deleted $successCount booking(s)")
                    refreshAllBookings()
                    clearSelection()
                }
            }
        }
    }

    // Compose/Screen helpers
    fun setErrorMsg(msg: String?) { _errorMsg.value = msg }
    fun setToastMsg(msg: String?) { toastMsg.value = msg }
    fun setShowExportDialog(show: Boolean) { showExportDialog.value = show }
    fun toggleSelection(id: String) {
        selection.value = if (selection.value.contains(id)) selection.value - id else selection.value + id
    }
    fun clearSelection() { selection.value = emptySet() }
}
