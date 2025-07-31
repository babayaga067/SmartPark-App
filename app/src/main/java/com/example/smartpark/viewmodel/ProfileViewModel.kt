package com.example.smartpark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpark.model.UserModel
import com.example.smartpark.repository.AuthRepository
import com.example.smartpark.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: UserModel) : ProfileState()
    data class Error(val message: String) : ProfileState()
    object UpdateSuccess : ProfileState()
    data class UpdateError(val message: String) : ProfileState()
    object LoggedOut : ProfileState()
    object Deleted : ProfileState()
}

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

    // Editable fields for form state
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _phoneError = MutableStateFlow<String?>(null)
    val phoneError: StateFlow<String?> = _phoneError

    private var currentUser: UserModel? = null

    fun loadProfile() {
        _profileState.value = ProfileState.Loading
        val firebaseUser = authRepository.getCurrentUser()
        if (firebaseUser == null) {
            _profileState.value = ProfileState.Error("No logged in user.")
            return
        }
        viewModelScope.launch {
            val result = userRepository.getUser(firebaseUser.uid)
            if (result.isSuccess) {
                currentUser = result.getOrNull()
                _profileState.value = ProfileState.Success(currentUser!!)
                // Initialize fields
                onNameChange(currentUser!!.name)
                onPhoneChange(currentUser!!.phone)
            } else {
                _profileState.value = ProfileState.Error(result.exceptionOrNull()?.message ?: "Profile load failed")
            }
        }
    }

    fun onNameChange(new: String) { _name.value = new; _nameError.value = null }
    fun onPhoneChange(new: String) { _phone.value = new; _phoneError.value = null }

    fun validateAndUpdateProfile() {
        val name = _name.value.trim()
        val phone = _phone.value.trim()
        var isValid = true
        if (name.isBlank()) { _nameError.value = "Name can't be blank."; isValid = false }
        if (phone.isBlank() || phone.length < 8) { _phoneError.value = "Enter valid phone number."; isValid = false }
        if (!isValid) return
        updateProfile(name, phone)
    }

    private fun updateProfile(name: String, phone: String) {
        val base = currentUser ?: return
        _profileState.value = ProfileState.Loading
        val updated = base.copy(name = name, phone = phone)
        viewModelScope.launch {
            val res = userRepository.updateUser(updated)
            if (res.isSuccess) {
                currentUser = updated
                _profileState.value = ProfileState.UpdateSuccess
                // Optionally: Call loadProfile() to refresh, or keep in sync via local assignment.
            } else {
                _profileState.value = ProfileState.UpdateError(res.exceptionOrNull()?.message ?: "Update failed")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _profileState.value = ProfileState.LoggedOut
    }

    fun deleteAccount() {
        val firebaseUser = authRepository.getCurrentUser()
        if (firebaseUser == null) {
            _profileState.value = ProfileState.Error("No logged in user.")
            return
        }
        viewModelScope.launch {
            val dbResult = userRepository.deleteUser(firebaseUser.uid)
            val authResult = runCatching { firebaseUser.delete() }
            if (dbResult.isSuccess && authResult.isSuccess) {
                _profileState.value = ProfileState.Deleted
            } else {
                val msg = dbResult.exceptionOrNull()?.message ?: authResult.exceptionOrNull()?.message ?: "Account delete failed"
                _profileState.value = ProfileState.Error(msg)
            }
        }
    }

    fun resetState() {
        _profileState.value = ProfileState.Idle
        _nameError.value = null
        _phoneError.value = null
    }
}
