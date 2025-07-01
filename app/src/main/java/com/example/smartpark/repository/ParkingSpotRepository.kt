package com.example.smartpark.repository

import com.example.smartpark.model.ParkingSpotModel
import com.google.firebase.database.*

class ParkingSpotRepository {

    private val database: FirebaseDatabase
        get() = FirebaseDatabase.getInstance()

    private val spotsRef: DatabaseReference
        get() = database.getReference("parking_spots")

    // Create a new parking spot
    fun addParkingSpot(spot: ParkingSpotModel, callback: (Boolean, String) -> Unit) {
        val key = spotsRef.push().key ?: return callback(false, "Unable to generate key")
        spot.id = key
        spotsRef.child(key).setValue(spot)
            .addOnSuccessListener { callback(true, "Spot added successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to add spot") }
    }

    // Read all parking spots
    fun getAllSpots(onDataChange: (List<ParkingSpotModel>) -> Unit) {
        spotsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val spots = mutableListOf<ParkingSpotModel>()
                for (child in snapshot.children) {
                    val spot = child.getValue(ParkingSpotModel::class.java)
                    spot?.let { spots.add(it) }
                }
                onDataChange(spots)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataChange(emptyList())
            }
        })
    }

    // Update an existing parking spot
    fun updateSpot(spot: ParkingSpotModel, callback: (Boolean, String) -> Unit) {
        if (spot.id.isBlank()) return callback(false, "Invalid spot ID")
        spotsRef.child(spot.id).setValue(spot)
            .addOnSuccessListener { callback(true, "Spot updated successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to update spot") }
    }

    // Delete a parking spot
    fun deleteSpot(id: String, callback: (Boolean, String) -> Unit) {
        if (id.isBlank()) return callback(false, "Invalid spot ID")
        spotsRef.child(id).removeValue()
            .addOnSuccessListener { callback(true, "Spot deleted successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to delete spot") }
    }
}
