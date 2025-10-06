package com.evcharge.mobile.util

import kotlinx.serialization.json.Json
import com.evcharge.mobile.data.remote.*
import kotlinx.serialization.encodeToString

object Json {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }
    
    // Specific encode functions for our DTOs
    fun encodeLoginRequest(value: LoginRequest): String {
        return json.encodeToString(value)
    }
    
    fun encodeBookingRequest(value: BookingRequest): String {
        return json.encodeToString(value)
    }
    
    fun encodeOwnerUpdateRequest(value: OwnerUpdateRequest): String {
        return json.encodeToString(value)
    }
    
    // Specific decode functions for our DTOs
    fun decodeLoginResponse(string: String): LoginResponse {
        return json.decodeFromString<LoginResponse>(string)
    }
    
    fun decodeRefreshResponse(string: String): RefreshResponse {
        return json.decodeFromString<RefreshResponse>(string)
    }
    
    fun decodeBookingResponse(string: String): BookingResponse {
        return json.decodeFromString<BookingResponse>(string)
    }
    
    fun decodeBookingResponseList(string: String): List<BookingResponse> {
        return json.decodeFromString<List<BookingResponse>>(string)
    }
    
    fun decodeStationResponse(string: String): StationResponse {
        return json.decodeFromString<StationResponse>(string)
    }
    
    fun decodeStationResponseList(string: String): List<StationResponse> {
        return json.decodeFromString<List<StationResponse>>(string)
    }
    
    fun decodeOwnerResponse(string: String): OwnerResponse {
        return json.decodeFromString<OwnerResponse>(string)
    }
    
    fun decodeDashboardCountsResponse(string: String): DashboardCountsResponse {
        return json.decodeFromString<DashboardCountsResponse>(string)
    }
    
    fun decodeBookingCompleteResponse(string: String): BookingCompleteResponse {
        return json.decodeFromString<BookingCompleteResponse>(string)
    }
    
    // Additional functions for SyncWorker
    fun decodeBookingRequest(string: String): BookingRequest {
        return json.decodeFromString<BookingRequest>(string)
    }
    
    fun decodeOwnerUpdateRequest(string: String): OwnerUpdateRequest {
        return json.decodeFromString<OwnerUpdateRequest>(string)
    }
}