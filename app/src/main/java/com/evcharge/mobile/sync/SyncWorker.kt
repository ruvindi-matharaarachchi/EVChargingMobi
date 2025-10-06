package com.evcharge.mobile.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.evcharge.mobile.data.local.DbHelper
import com.evcharge.mobile.data.local.dao.SyncQueueDao
import com.evcharge.mobile.data.local.model.SyncItem
import com.evcharge.mobile.data.remote.HttpClient
import com.evcharge.mobile.security.TokenStore
import com.evcharge.mobile.util.Json
import kotlinx.coroutines.delay

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val dbHelper = DbHelper(context)
    private val syncQueueDao = SyncQueueDao(dbHelper)
    private val tokenStore = TokenStore(context)
    private val httpClient = HttpClient()
    
    override suspend fun doWork(): Result {
        return try {
            // Set auth token if available
            tokenStore.getToken()?.let { token ->
                httpClient.setAuthToken(token)
            }
            
            val pendingItems = syncQueueDao.getPending()
            var successCount = 0
            
            for (item in pendingItems) {
                try {
                    val success = processSyncItem(item)
                    if (success) {
                        syncQueueDao.deleteById(item.id)
                        successCount++
                    } else {
                        syncQueueDao.incrementRetries(item.id)
                    }
                } catch (e: Exception) {
                    syncQueueDao.incrementRetries(item.id)
                }
                
                // Small delay between requests
                delay(100)
                
                // Force garbage collection every 10 items to prevent memory buildup
                if (successCount % 10 == 0) {
                    System.gc()
                }
            }
            
            if (successCount > 0) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private suspend fun processSyncItem(item: SyncItem): Boolean {
        return try {
            when (item.kind) {
                "create_booking" -> {
                    val request = Json.decodeBookingRequest(item.payload)
                    val result = httpClient.post("/api/booking", request)
                    result is com.evcharge.mobile.util.Result.Success
                }
                "update_booking" -> {
                    val parts = item.payload.split("|", limit = 2)
                    if (parts.size == 2) {
                        val bookingId = parts[0]
                        val request = Json.decodeBookingRequest(parts[1])
                        val result = httpClient.put("/api/booking/$bookingId", request)
                        result is com.evcharge.mobile.util.Result.Success
                    } else {
                        false
                    }
                }
                "cancel_booking" -> {
                    val result = httpClient.delete("/api/booking/${item.payload}")
                    result is com.evcharge.mobile.util.Result.Success
                }
                "update_owner" -> {
                    val parts = item.payload.split("|", limit = 2)
                    if (parts.size == 2) {
                        val nic = parts[0]
                        val request = Json.decodeOwnerUpdateRequest(parts[1])
                        val result = httpClient.put("/api/evowner/$nic", request)
                        result is com.evcharge.mobile.util.Result.Success
                    } else {
                        false
                    }
                }
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
}
