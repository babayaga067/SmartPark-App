package com.example.smartpark.repository

import com.example.smartpark.model.BookingModel
import com.google.firebase.database.FirebaseDatabase

interface BookingRepository {
    fun getBookingsForUser(userId: String, onComplete: (List<BookingModel>) -> Unit)
    fun getAllBookings(onComplete: (List<BookingModel>) -> Unit)
    fun addBooking(booking: BookingModel, callback: (Boolean, String) -> Unit)
    fun deleteBooking(bookingId: String, callback: (Boolean, String) -> Unit)
    fun updateBooking(booking: BookingModel, callback: (Boolean, String) -> Unit)
}

class BookingRepositoryImpl : BookingRepository {

    private val db = FirebaseDatabase.getInstance().getReference("bookings")

    override fun getBookingsForUser(userId: String, onComplete: (List<BookingModel>) -> Unit) {
        db.get().addOnSuccessListener { snapshot ->
            val list = snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) }
                .filter { it.userId == userId }
            onComplete(list)
        }.addOnFailureListener {
            onComplete(emptyList())
        }
    }

    override fun getAllBookings(onComplete: (List<BookingModel>) -> Unit) {
        db.get().addOnSuccessListener { snapshot ->
            val list = snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) }
            onComplete(list)
        }.addOnFailureListener {
            onComplete(emptyList())
        }
    }

    override fun addBooking(booking: BookingModel, callback: (Boolean, String) -> Unit) {
        val key = booking.id.ifBlank { db.push().key }
        if (key == null) return callback(false, "Unable to generate id")
        db.child(key).setValue(booking.copy(id = key))
            .addOnSuccessListener { callback(true, "Booking added") }
            .addOnFailureListener { callback(false, it.message ?: "Failed") }
    }

    override fun deleteBooking(bookingId: String, callback: (Boolean, String) -> Unit) {
        if (bookingId.isBlank()) return callback(false, "Invalid booking id")
        db.child(bookingId).removeValue()
            .addOnSuccessListener { callback(true, "Booking deleted") }
            .addOnFailureListener { callback(false, it.message ?: "Failed") }
    }

    override fun updateBooking(booking: BookingModel, callback: (Boolean, String) -> Unit) {
        if (booking.id.isBlank()) return callback(false, "Invalid booking id")
        db.child(booking.id).setValue(booking)
            .addOnSuccessListener { callback(true, "Booking updated") }
            .addOnFailureListener { callback(false, it.message ?: "Failed") }
    }
}
