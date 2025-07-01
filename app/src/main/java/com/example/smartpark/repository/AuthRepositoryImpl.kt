package com.example.smartpark.repository

import android.util.Log.e
import com.example.smartpark.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    override suspend fun register(
        email: String,
        password: String
    ): Result<Unit> {
       return try {
           auth.createUserWithEmailAndPassword(email, password).await()
           Result.success(Unit)
       }catch(e: Exception) {
           Result.failure(e)
       }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try{
        auth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e("AuthRepository", "Error sending Password reset email: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun logout() {
       auth.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
