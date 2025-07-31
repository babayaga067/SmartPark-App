package com.example.smartpark.viewmodel.adminViewModel

import androidx.lifecycle.ViewModel
import com.example.smartpark.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.smartpark.repository.UserRepository

class ManageUsersViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserModel>>(emptyList())
    val users: StateFlow<List<UserModel>> = _users

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchUsers() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = userRepository.getAllUsers()
            result.onSuccess { list ->
                _users.value = list
            }.onFailure { e ->
                _error.value = e.message ?: "Failed to load users"
                _users.value = emptyList()
            }
            _loading.value = false
        }
    }

    fun addUser(user: UserModel, onDone: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = userRepository.addUser(user)
            if (result.isSuccess) {
                fetchUsers()
                onDone(true, "User added successfully")
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Failed to add user"
                _error.value = msg
                onDone(false, msg)
            }
            _loading.value = false
        }
    }

    fun updateUser(user: UserModel, onDone: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = userRepository.updateUser(user)
            if (result.isSuccess) {
                fetchUsers()
                onDone(true, "User updated successfully")
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Failed to update user"
                _error.value = msg
                onDone(false, msg)
            }
            _loading.value = false
        }
    }

    fun deleteUser(userId: String, onDone: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = userRepository.deleteUser(userId)
            if (result.isSuccess) {
                fetchUsers()
                onDone(true, "User deleted successfully")
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Failed to delete user"
                _error.value = msg
                onDone(false, msg)
            }
            _loading.value = false
        }
    }

    fun sendResetPassword(email: String, onDone: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            _loading.value = true
            val result = try {
                userRepository.sendPasswordResetEmail(email)
            } catch (e: Exception) {
                Result.failure<Unit>(e)
            }
            if (result.isSuccess) {
                onDone(true, "Password reset email sent to $email")
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Failed to send reset email"
                _error.value = msg
                onDone(false, msg)
            }
            _loading.value = false
        }
    }
}