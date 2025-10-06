package com.evcharge.mobile.domain.repositories

import com.evcharge.mobile.data.remote.LoginRequest
import com.evcharge.mobile.data.remote.LoginResponse
import com.evcharge.mobile.data.remote.RefreshResponse
import com.evcharge.mobile.util.Result

interface AuthRepository {
    suspend fun login(nic: String, password: String): Result<LoginResponse>
    suspend fun refreshToken(): Result<RefreshResponse>
    fun isLoggedIn(): Boolean
    fun logout()
    fun getCurrentNic(): String?
    fun getCurrentRole(): String?
}
