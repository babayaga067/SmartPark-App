package com.example.smartpark.viewmodel

import androidx.lifecycle.ViewModel
import com.example.smartpark.model.BookingModel
import com.example.smartpark.repository.AuthRepository
import com.example.smartpark.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BookingViewModel(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _userBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val userBookings: StateFlow<List<BookingModel>> = _userBookings

    private val _allBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val allBookings: StateFlow<List<BookingModel>> = _allBookings

    fun loadUserBookings() {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        bookingRepository.getBookingsForUser(userId) { bookings ->
            _userBookings.value = bookings
        }
    }

    fun loadAllBookings() {
        bookingRepository.getAllBookings { bookings ->
            _allBookings.value = bookings
        }
    }
}
