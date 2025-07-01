package com.example.smartpark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpark.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()

    private val _resetState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val resetState: StateFlow<ForgotPasswordState> = _resetState

    fun sendResetEmail(email: String) {
        _resetState.value = ForgotPasswordState.Loading

        viewModelScope.launch {
            val result = repository.resetPassword(email)
            _resetState.value = if (result.isSuccess) {
                ForgotPasswordState.Success
            } else {
                ForgotPasswordState.Error(result.exceptionOrNull()?.message ?: "Failed to send reset email")
            }
        }
    }

    fun resetState() {
        _resetState.value = ForgotPasswordState.Idle
    }
}

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}
