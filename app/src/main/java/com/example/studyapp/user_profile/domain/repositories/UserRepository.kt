package com.example.studyapp.user_profile.domain.repositories

import com.example.studyapp.user_profile.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: User): Int
    suspend fun getUserById(userId: Int): User?
    fun getUserFlow(userId: Int): Flow<User?>
    suspend fun updateUser(user: User)
}