package com.example.smartpark.viewmodel.adminViewModel

import androidx.lifecycle.ViewModel
import com.example.smartpark.repository.AuthRepository
import com.example.smartpark.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminDashboardViewModel(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _adminEmail = MutableStateFlow<String?>(null)
    val adminEmail: StateFlow<String?> = _adminEmail

    private val _totalRevenue = MutableStateFlow(0)
    val totalRevenue: StateFlow<Int> = _totalRevenue

    fun loadAdminInfo() {
        _adminEmail.value = authRepository.getCurrentUser()?.email
    }

    fun fetchTotalRevenue() {
        bookingRepository.getAllBookings { bookings ->
            _totalRevenue.value = bookings.sumOf { it.price }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
