package com.evcharge.mobile.data.remote

import com.evcharge.mobile.util.Json
import com.evcharge.mobile.util.Result

class OwnerApi(private val httpClient: HttpClient) {
    
    suspend fun updateOwner(nic: String, request: OwnerUpdateRequest): Result<OwnerResponse> {
        val result = httpClient.put(ApiRoutes.ownerUpdate(nic), request)
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeOwnerResponse(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse owner update response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
    
    suspend fun deactivateOwner(nic: String): Result<String> {
        val result = httpClient.post(ApiRoutes.ownerDeactivate(nic))
        return result
    }
    
    suspend fun reactivateOwner(nic: String): Result<String> {
        val result = httpClient.post(ApiRoutes.ownerReactivate(nic))
        return result
    }
}
