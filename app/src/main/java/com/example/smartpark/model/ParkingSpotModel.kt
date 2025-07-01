package com.example.smartpark.model

data class ParkingSpotModel(
    var id: String = "",
    var location: String = "",
    var status: String = "Available", // or "Reserved"
    var reservedBy: String? = null
)
