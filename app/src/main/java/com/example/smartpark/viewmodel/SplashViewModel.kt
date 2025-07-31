package com.example.smartpark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpark.repository.AuthRepository
import com.example.smartpark.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

sealed class SplashUIState {
    object Loading : SplashUIState()
    data class Navigate(val isLoggedIn: Boolean, val isAdmin: Boolean?) : SplashUIState()
}

class SplashViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUIState>(SplashUIState.Loading)
    val uiState: StateFlow<SplashUIState> = _uiState

    init { checkLoginStatus() }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            // Give max 7s for full check (never leaks, always sets state)
            val result = withTimeoutOrNull(7000) {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.value = SplashUIState.Navigate(false, null)
                } else {
                    val roleResult = try { userRepository.getUserRole(user.uid) }
                    catch (e: Exception) { Result.failure<String>(e) }
                    if (roleResult.isSuccess && roleResult.getOrNull() == "Admin") {
                        _uiState.value = SplashUIState.Navigate(true, true)
                    } else if (roleResult.isSuccess) {
                        _uiState.value = SplashUIState.Navigate(true, false)
                    } else {
                        // Role fetch failed, but allow login (as user)
                        _uiState.value = SplashUIState.Navigate(true, false)
                    }
                }
            }
            if (result == null) {
                // Timed out: go to login
                _uiState.value = SplashUIState.Navigate(false, null)
            }
        }
    }
}
