package com.evcharge.mobile.domain.repositories

import com.evcharge.mobile.domain.entities.Station
import com.evcharge.mobile.util.Result

interface StationRepository {
    suspend fun getAllStations(): Result<List<Station>>
    suspend fun getStationById(id: String): Result<Station>
    suspend fun getNearbyStations(lat: Double, lng: Double, radius: Double = 10.0): Result<List<Station>>
    fun getCachedStations(): List<Station>
    fun cacheStations(stations: List<Station>)
}
