package com.example.smartpark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpark.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf

class LoginViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()

    //  Validation Error States (unchanged from your original code)
    val emailError = mutableStateOf<String?>(null)
    val passwordError = mutableStateOf<String?>(null)

    //  Backend login state to communicate with Compose UI
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    //  Input Validator (same as your original logic)
    fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        emailError.value = if (email.isBlank()) {
            isValid = false
            "Email can't be empty"
        } else null

        passwordError.value = if (password.length < 6) {
            isValid = false
            "Password too short"
        } else null

        return isValid
    }

    //  Backend Login Using Repository + Coroutines + Result
    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = repository.login(email, password)

            _loginState.value = if (result.isSuccess) {
                LoginState.Success
            } else {
                LoginState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    //  Optional: reset the state after showing message
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

//  Sealed class for backend response states
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
