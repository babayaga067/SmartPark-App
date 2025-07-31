package com.example.smartpark.repository

import com.example.smartpark.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

interface UserRepository {
    suspend fun getUserRole(uid: String): Result<String>
    suspend fun getUser(uid: String): Result<UserModel>
    suspend fun saveUser(user: UserModel): Result<Unit>
    suspend fun getAllUsers(): Result<List<UserModel>>
    suspend fun addUser(user: UserModel): Result<Unit>
    suspend fun updateUser(user: UserModel): Result<Unit>
    suspend fun deleteUser(uid: String): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}

class UserRepositoryImpl(
    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("users"),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : UserRepository {

    override suspend fun getUserRole(uid: String): Result<String> = try {
        val snapshot = db.child(uid).child("role").get().await()
        val value = snapshot.getValue(String::class.java)
        if (value != null) Result.success(value)
        else Result.failure(Exception("No role found for user $uid"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUser(uid: String): Result<UserModel> = try {
        val snapshot = db.child(uid).get().await()
        val user = snapshot.getValue(UserModel::class.java)
        if (user != null) Result.success(user)
        else Result.failure(Exception("User with uid $uid not found"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun saveUser(user: UserModel): Result<Unit> = try {
        db.child(user.id).setValue(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAllUsers(): Result<List<UserModel>> = try {
        val snapshot = db.get().await()
        val list = snapshot.children.mapNotNull { it.getValue(UserModel::class.java) }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addUser(user: UserModel): Result<Unit> = try {
        val checkSnapshot = db.child(user.id).get().await()
        if (checkSnapshot.exists()) {
            Result.failure(Exception("User with id ${user.id} already exists"))
        } else {
            db.child(user.id).setValue(user).await()
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateUser(user: UserModel): Result<Unit> = try {
        db.child(user.id).updateChildren(user.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteUser(uid: String): Result<Unit> = try {
        db.child(uid).removeValue().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// Helper for updateChildren
fun UserModel.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "email" to email,
    "phone" to phone,
    "profileImageUrl" to profileImageUrl,
    "role" to role,
    "registeredAt" to registeredAt
)
