package com.example.studyapp.user_profile.domain.repositories

import com.example.studyapp.user_profile.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: User): Int
    suspend fun getUserById(userId: Int): User?
    fun getUserFlow(userId: Int): Flow<User?>
    fun getAllUsers(): Flow<List<User>>
    suspend fun updateUser(user: User)
    suspend fun updateUserTheme(userId: Int, theme: String)
    suspend fun updateEquippedCosmetics(userId: Int, iconId: Int?, bannerId: Int?)
    suspend fun deleteUser(userId: Int)
    suspend fun checkAndResetStreak(userId: Int)
}