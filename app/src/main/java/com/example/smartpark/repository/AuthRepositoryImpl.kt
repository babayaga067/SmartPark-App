package com.example.smartpark.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override suspend fun register(email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            // result.user is now the current user (or null)
            if (auth.currentUser != null) {
                AuthResult.Success
            } else {
                AuthResult.Error("Registration succeeded but user is null.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message)
        }
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (auth.currentUser != null) {
                AuthResult.Success
            } else {
                AuthResult.Error("Login succeeded but user is null.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    // This is fine!
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
