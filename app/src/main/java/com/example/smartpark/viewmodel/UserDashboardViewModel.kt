package com.example.smartpark.viewmodel

import androidx.lifecycle.ViewModel
import com.example.smartpark.model.BookingModel
import com.example.smartpark.model.ParkingSpotModel
import com.example.smartpark.repository.AuthRepository
import com.example.smartpark.repository.BookingRepository
import com.example.smartpark.repository.ParkingSpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserDashboardViewModel(
    private val authRepository: AuthRepository,
    private val spotRepository: ParkingSpotRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> get() = _userEmail

    private val _parkingSpots = MutableStateFlow<List<ParkingSpotModel>>(emptyList())
    val parkingSpots: StateFlow<List<ParkingSpotModel>> get() = _parkingSpots

    fun loadUserInfo() {
        _userEmail.value = authRepository.getCurrentUser()?.email
    }

    fun fetchAvailableSpots() {
        spotRepository.getAllSpots { spots ->
            _parkingSpots.value = spots.filter { it.status == "Available" }
        }
    }

    fun bookSpot(spotId: String, price: Int, onResult: (Boolean, String) -> Unit) {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId == null) {
            onResult(false, "User not logged in")
            return
        }
        val booking = BookingModel(
            id = "",
            userId = userId,
            spotId = spotId,
            price = price,
            timestamp = System.currentTimeMillis(),
            status = "Active"
        )
        bookingRepository.addBooking(booking) { success, message ->
            if (success) {
                spotRepository.updateSpot(
                    ParkingSpotModel(
                        id = spotId,
                        name = "",
                        price = price,
                        location = "",
                        status = "Booked"
                    )
                ) { _, _ -> }
            }
            onResult(success, message)
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
