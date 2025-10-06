package com.evcharge.mobile.data.remote

import com.evcharge.mobile.util.Json
import com.evcharge.mobile.util.Result

class AuthApi(private val httpClient: HttpClient) {
    
    suspend fun login(nic: String, password: String): Result<LoginResponse> {
        val request = LoginRequest(nic, password)
        val result = httpClient.post(ApiRoutes.LOGIN, request)
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeLoginResponse(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse login response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
    
    suspend fun refreshToken(): Result<RefreshResponse> {
        val result = httpClient.post(ApiRoutes.REFRESH)
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeRefreshResponse(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse refresh response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
}
