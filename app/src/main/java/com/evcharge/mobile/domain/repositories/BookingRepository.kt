package com.evcharge.mobile.domain.repositories

import com.evcharge.mobile.data.remote.BookingRequest
import com.evcharge.mobile.domain.entities.DashboardCounts
import com.evcharge.mobile.domain.entities.Reservation
import com.evcharge.mobile.util.Result

interface BookingRepository {
    suspend fun createBooking(request: BookingRequest): Result<Reservation>
    suspend fun getBookingById(id: String): Result<Reservation>
    suspend fun getBookingsByOwner(nic: String): Result<List<Reservation>>
    suspend fun getDashboardCounts(nic: String): Result<DashboardCounts>
    suspend fun updateBooking(id: String, request: BookingRequest): Result<Reservation>
    suspend fun cancelBooking(id: String): Result<String>
    suspend fun completeBooking(qrPayload: String): Result<String>
    
    // Local cache methods
    fun getCachedBookings(ownerNic: String): List<Reservation>
    fun cacheBooking(reservation: Reservation)
    fun removeCachedBooking(id: String)
}
