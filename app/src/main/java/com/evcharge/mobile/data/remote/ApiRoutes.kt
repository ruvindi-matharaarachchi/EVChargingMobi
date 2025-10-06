package com.evcharge.mobile.data.remote

object ApiRoutes {
    
    // Auth endpoints
    const val LOGIN = "/api/auth/login"
    const val REFRESH = "/api/auth/refresh"
    
    // EV Owner endpoints
    const val OWNER_UPDATE = "/api/evowner/{nic}"
    const val OWNER_DEACTIVATE = "/api/evowner/{nic}/deactivate"
    const val OWNER_REACTIVATE = "/api/evowner/{nic}/reactivate"
    
    // Station endpoints
    const val STATIONS = "/api/station"
    const val STATION_BY_ID = "/api/station/{id}"
    const val STATIONS_NEARBY = "/api/station/nearby"
    
    // Booking endpoints
    const val BOOKINGS = "/api/booking"
    const val BOOKING_BY_ID = "/api/booking/{id}"
    const val BOOKINGS_BY_OWNER = "/api/booking/owner/{nic}"
    const val BOOKING_DASHBOARD = "/api/booking/dashboard/{nic}"
    const val BOOKING_APPROVE = "/api/booking/{id}/approve"
    const val BOOKING_COMPLETE = "/api/booking/complete"
    
    // Helper methods for URL building
    fun ownerUpdate(nic: String) = OWNER_UPDATE.replace("{nic}", nic)
    fun ownerDeactivate(nic: String) = OWNER_DEACTIVATE.replace("{nic}", nic)
    fun ownerReactivate(nic: String) = OWNER_REACTIVATE.replace("{nic}", nic)
    fun stationById(id: String) = STATION_BY_ID.replace("{id}", id)
    fun bookingsByOwner(nic: String) = BOOKINGS_BY_OWNER.replace("{nic}", nic)
    fun bookingDashboard(nic: String) = BOOKING_DASHBOARD.replace("{nic}", nic)
    fun bookingById(id: String) = BOOKING_BY_ID.replace("{id}", id)
    fun bookingApprove(id: String) = BOOKING_APPROVE.replace("{id}", id)
}
