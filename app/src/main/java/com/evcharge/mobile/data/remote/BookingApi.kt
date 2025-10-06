package com.evcharge.mobile.data.remote

import com.evcharge.mobile.util.Json
import com.evcharge.mobile.util.Result

class BookingApi(private val httpClient: HttpClient) {
    
    suspend fun createBooking(request: BookingRequest): Result<BookingResponse> {
        val result = httpClient.post(ApiRoutes.BOOKINGS, request)
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeBookingResponse(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse create booking response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
    
    suspend fun getBookingById(id: String): Result<BookingResponse> {
        val result = httpClient.get(ApiRoutes.bookingById(id))
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeBookingResponse(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse booking response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
    
    suspend fun getBookingsByOwner(nic: String): Result<List<BookingResponse>> {
        val result = httpClient.get(ApiRoutes.bookingsByOwner(nic))
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeBookingResponseList(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse owner bookings response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
    
    suspend fun getDashboardCounts(nic: String): Result<DashboardCountsResponse> {
        val result = httpClient.get(ApiRoutes.bookingDashboard(nic))
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeDashboardCountsResponse(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse dashboard response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
    
    suspend fun updateBooking(id: String, request: BookingRequest): Result<BookingResponse> {
        val result = httpClient.put(ApiRoutes.bookingById(id), request)
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeBookingResponse(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse update booking response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
    
    suspend fun cancelBooking(id: String): Result<String> {
        val result = httpClient.delete(ApiRoutes.bookingById(id))
        return result
    }
    
    suspend fun completeBooking(qrPayload: String): Result<BookingCompleteResponse> {
        val request = BookingCompleteRequest(qrPayload)
        val result = httpClient.post(ApiRoutes.BOOKING_COMPLETE, request)
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeBookingCompleteResponse(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse complete booking response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
}
