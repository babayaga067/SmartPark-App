package com.example.smartpark.model

data class BookingModel(
    val id: String = "",
    val userId: String = "",
    val spotId: String = "",
    val price: Int = 0,
    val timestamp: Long = 0L,
    val status: String = "Active"   // "Active", "Completed", "Cancelled", etc.
)
