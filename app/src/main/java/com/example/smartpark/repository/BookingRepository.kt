package com.example.smartpark.repository

import com.example.smartpark.model.BookingModel

interface BookingRepository {
    fun getBookingsForUser(userId: String, onComplete: (List<BookingModel>) -> Unit)
    fun getAllBookings(onComplete: (List<BookingModel>) -> Unit)
    fun addBooking(booking: BookingModel, callback: (Boolean, String) -> Unit)
    fun deleteBooking(bookingId: String, callback: (Boolean, String) -> Unit)
    fun updateBooking(booking: BookingModel, callback: (Boolean, String) -> Unit)
}
