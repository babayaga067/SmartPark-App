package com.example.smartpark.viewmodel.adminViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpark.repository.AuthRepository
import com.example.smartpark.repository.BookingRepository
import com.example.smartpark.repository.UserRepository
import com.example.smartpark.repository.ParkingSpotRepository
import com.example.smartpark.view.admin.RecentBooking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AdminDashboardViewModel(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val userRepository: UserRepository,
    private val spotRepository: ParkingSpotRepository
) : ViewModel() {

    private val _adminEmail = MutableStateFlow<String?>(null)
    val adminEmail: StateFlow<String?> = _adminEmail

    private val _totalRevenue = MutableStateFlow<Int?>(null)
    val totalRevenue: StateFlow<Int?> = _totalRevenue

    private val _totalBookings = MutableStateFlow<Int?>(null)
    val totalBookings: StateFlow<Int?> = _totalBookings

    private val _availableSpots = MutableStateFlow<Int?>(null)
    val availableSpots: StateFlow<Int?> = _availableSpots

    private val _totalUsers = MutableStateFlow<Int?>(null)
    val totalUsers: StateFlow<Int?> = _totalUsers

    private val _pendingActions = MutableStateFlow(0)
    val pendingActions: StateFlow<Int> = _pendingActions

    private val _recentBookings = MutableStateFlow<List<RecentBooking>>(emptyList())
    val recentBookings: StateFlow<List<RecentBooking>> = _recentBookings

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg

    private val _lastRefreshed = MutableStateFlow(System.currentTimeMillis())
    val lastRefreshed: StateFlow<Long> = _lastRefreshed

    fun loadAdminInfo() {
        _adminEmail.value = authRepository.getCurrentUser()?.email
    }

    fun loadDashboardStats() {
        _isLoading.value = true
        _errorMsg.value = null
        viewModelScope.launch {
            try {
                bookingRepository.getAllBookings { bookings ->
                    _totalBookings.value = bookings.size
                    _totalRevenue.value = bookings.sumOf { it.price }
                    _recentBookings.value = bookings.sortedByDescending { it.timestamp }
                        .take(5)
                        .map {
                            RecentBooking(
                                id = it.id,
                                name = it.userId, // Replace with user name if needed
                                plate = it.spotId, // Replace with vehicle/plate if needed
                                status = it.status,
                                date = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault()).format(Date(it.timestamp))
                            )
                        }
                }

                viewModelScope.launch {
                    userRepository.getAllUsers()
                        .onSuccess { _totalUsers.value = it.size }
                        .onFailure { _totalUsers.value = null }
                }

                spotRepository.getAllSpots { spots ->
                    _availableSpots.value = spots.count { it.status.equals("Available", true) }
                }

                bookingRepository.getAllBookings { bookings ->
                    _pendingActions.value = bookings.count { it.status.equals("Active", true) }
                }

                _lastRefreshed.value = System.currentTimeMillis()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMsg.value = "Failed to load dashboard: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _errorMsg.value = null }
    fun logout() { authRepository.logout() }
}
