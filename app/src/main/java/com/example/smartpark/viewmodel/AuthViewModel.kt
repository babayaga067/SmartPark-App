package com.example.smartpark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpark.repository.AuthRepository
import com.example.smartpark.repository.AuthResult
import com.example.smartpark.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val loginState: StateFlow<UiState<String>> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginState.value = when (result) {
                is AuthResult.Success -> UiState.Success("Login Successful")
                is AuthResult.Error -> UiState.Error(result.message ?: "Unknown error")
            }
        }
    }
}
