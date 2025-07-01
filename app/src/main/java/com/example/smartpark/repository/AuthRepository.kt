package com.example.smartpark.repository

interface AuthRepository {
    //function to register new user(email + password)
    suspend fun register(email: String, password: String): Result<Unit>

    //function to login existing user
    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun resetPassword(email: String): Result<Unit>

    suspend fun logout()

    fun isUserLoggedIn(): Boolean


}