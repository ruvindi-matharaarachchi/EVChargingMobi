package com.evcharge.mobile

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.Configuration
import androidx.work.WorkManager
import com.evcharge.mobile.data.local.DbHelper
import com.evcharge.mobile.sync.SyncWorkerFactory

class App : Application(), Configuration.Provider {
    
    companion object {
        lateinit var instance: App
            private set
    }
    
    val dbHelper by lazy { DbHelper(this) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // WorkManager is automatically initialized by Android
        // We don't need to initialize it manually
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(SyncWorkerFactory())
            .setMaxSchedulerLimit(1) // Limit concurrent workers
            .build()
    }
    
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        // Clear caches when memory is low
        System.gc()
    }
    
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                // Clear all caches
                System.gc()
            }
            TRIM_MEMORY_RUNNING_LOW -> {
                // Clear some caches
                System.gc()
            }
        }
    }
}
