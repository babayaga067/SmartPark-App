package com.example.smartpark.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String?) : AuthResult()
}

interface AuthRepository {
    suspend fun register(email: String, password: String): AuthResult
    suspend fun login(email: String, password: String): AuthResult
    fun logout()
    fun getCurrentUser(): FirebaseUser?
    suspend fun sendPasswordResetEmail(email: String): AuthResult
}

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {
    override suspend fun register(email: String, password: String): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            if (auth.currentUser != null) AuthResult.Success
            else AuthResult.Error("Registration succeeded but user is null.")
        } catch (e: Exception) {
            AuthResult.Error(e.message)
        }
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            if (auth.currentUser != null) AuthResult.Success
            else AuthResult.Error("Login succeeded but user is null.")
        } catch (e: Exception) {
            AuthResult.Error(e.message)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message)
        }
    }
}
