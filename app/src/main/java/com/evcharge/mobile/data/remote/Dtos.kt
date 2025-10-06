package com.evcharge.mobile.data.remote

import kotlinx.serialization.Serializable

// Auth DTOs
@Serializable
data class LoginRequest(
    val nic: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val expiresAt: String,
    val role: String,
    val nic: String,
    val name: String
)

@Serializable
data class RefreshResponse(
    val token: String,
    val expiresAt: String
)

// Owner DTOs
@Serializable
data class OwnerUpdateRequest(
    val name: String,
    val email: String?,
    val phone: String?
)

@Serializable
data class OwnerResponse(
    val nic: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val isActive: Boolean
)

// Station DTOs
@Serializable
data class StationResponse(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: String?,
    val slots: Int,
    val isActive: Boolean
)

@Serializable
data class NearbyStationsRequest(
    val lat: Double,
    val lng: Double,
    val radius: Double
)

// Booking DTOs
@Serializable
data class BookingRequest(
    val stationId: String,
    val startTime: String,
    val endTime: String
)

@Serializable
data class BookingResponse(
    val id: String,
    val ownerNic: String,
    val stationId: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val isApproved: Boolean,
    val qrPayload: String?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class BookingCompleteRequest(
    val qrPayload: String
)

@Serializable
data class BookingCompleteResponse(
    val success: Boolean,
    val message: String,
    val bookingId: String?
)

@Serializable
data class DashboardCountsResponse(
    val pending: Int,
    val approved: Int
)

// Error DTOs
@Serializable
data class ErrorResponse(
    val message: String,
    val details: String?
)

// Pagination DTOs
@Serializable
data class PaginationRequest(
    val page: Int = 1,
    val pageSize: Int = 10
)

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)
