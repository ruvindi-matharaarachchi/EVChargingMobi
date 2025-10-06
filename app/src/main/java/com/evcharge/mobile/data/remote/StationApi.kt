package com.evcharge.mobile.data.remote

import com.evcharge.mobile.util.Json
import com.evcharge.mobile.util.Result

class StationApi(private val httpClient: HttpClient) {
    
    suspend fun getAllStations(): Result<List<StationResponse>> {
        val result = httpClient.get(ApiRoutes.STATIONS)
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeStationResponseList(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse stations response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
    
    suspend fun getStationById(id: String): Result<StationResponse> {
        val result = httpClient.get(ApiRoutes.stationById(id))
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeStationResponse(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse station response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
    
    suspend fun getNearbyStations(lat: Double, lng: Double, radius: Double = 10.0): Result<List<StationResponse>> {
        val params = mapOf(
            "lat" to lat.toString(),
            "lng" to lng.toString(),
            "radius" to radius.toString()
        )
        val result = httpClient.get(ApiRoutes.STATIONS_NEARBY, params)
        
        return when (result) {
            is Result.Success -> {
                try {
                    val response = Json.decodeStationResponseList(result.data)
                    Result.Success(response)
                } catch (e: Exception) {
                    Result.Error("Failed to parse nearby stations response: ${e.message}")
                }
            }
            is Result.Error -> result
        }
    }
}
