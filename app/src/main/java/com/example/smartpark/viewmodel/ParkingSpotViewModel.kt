package com.example.smartpark.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartpark.model.ParkingSpotModel
import com.example.smartpark.repository.ParkingSpotRepository

class ParkingSpotViewModel : ViewModel() {

    private val repository = ParkingSpotRepository()

    private val _spots = MutableLiveData<List<ParkingSpotModel>>()
    val spots: LiveData<List<ParkingSpotModel>> get() = _spots

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchAllSpots()
    }

    // Fetch all spots and update LiveData
    fun fetchAllSpots() {
        _isLoading.value = true
        repository.getAllSpots { list ->
            _spots.value = list
            _isLoading.value = false
        }
    }

    // Add a new spot
    fun addSpot(spot: ParkingSpotModel, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repository.addParkingSpot(spot) { success, message ->
            if (success) fetchAllSpots()
            _isLoading.value = false
            callback(success, message)
        }
    }

    // Update a spot
    fun updateSpot(spot: ParkingSpotModel, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repository.updateSpot(spot) { success, message ->
            if (success) fetchAllSpots()
            _isLoading.value = false
            callback(success, message)
        }
    }

    // Delete a spot
    fun deleteSpot(id: String, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repository.deleteSpot(id) { success, message ->
            if (success) fetchAllSpots()
            _isLoading.value = false
            callback(success, message)
        }
    }
}
