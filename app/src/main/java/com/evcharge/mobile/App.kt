package com.evcharge.mobile

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.evcharge.mobile.data.local.DbHelper
import com.evcharge.mobile.sync.SyncWorkerFactory

class App : Application() {
    
    companion object {
        lateinit var instance: App
            private set
    }
    
    val dbHelper by lazy { DbHelper(this) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize WorkManager with custom factory for sync
        val config = Configuration.Builder()
            .setWorkerFactory(SyncWorkerFactory())
            .build()
        
        WorkManager.initialize(this, config)
    }
}
