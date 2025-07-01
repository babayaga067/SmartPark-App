package com.example.smartpark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpark.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()

    // ✅ Input field validation states
    val nameError = mutableStateOf<String?>(null)
    val emailError = mutableStateOf<String?>(null)
    val passwordError = mutableStateOf<String?>(null)
    val confirmPasswordError = mutableStateOf<String?>(null)

    // ✅ StateFlow to track backend registration state
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    // ✅ Validation logic (kept from your original code)
    fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        nameError.value = if (name.isBlank()) {
            isValid = false
            "Name can't be empty"
        } else null

        emailError.value = if (email.isBlank()) {
            isValid = false
            "Email can't be empty"
        } else null

        passwordError.value = if (password.length < 6) {
            isValid = false
            "Password must be at least 6 characters"
        } else null

        confirmPasswordError.value = if (confirmPassword != password) {
            isValid = false
            "Passwords do not match"
        } else null

        return isValid
    }

    // ✅ Backend registration using repository
    fun registerUser(email: String, password: String) {
        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            val result = repository.register(email, password)

            _registerState.value = if (result.isSuccess) {
                RegisterState.Success
            } else {
                RegisterState.Error(result.exceptionOrNull()?.message ?: "Registration Failed")
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}

// ✅ Sealed class for UI state communication
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}
