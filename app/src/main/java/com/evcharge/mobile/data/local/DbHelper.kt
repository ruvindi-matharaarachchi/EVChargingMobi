package com.evcharge.mobile.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.evcharge.mobile.data.local.model.LocalUser
import com.evcharge.mobile.data.local.model.ReservationLocal
import com.evcharge.mobile.data.local.model.StationLocal
import com.evcharge.mobile.data.local.model.SyncItem

class DbHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    
    companion object {
        private const val DATABASE_NAME = "ev_charging.db"
        private const val DATABASE_VERSION = 1
        
        // Users table
        private const val CREATE_USERS_TABLE = """
            CREATE TABLE users (
                nic TEXT PRIMARY KEY,
                role TEXT NOT NULL,
                name TEXT NOT NULL,
                email TEXT,
                phone TEXT,
                is_active INTEGER NOT NULL DEFAULT 1,
                updated_at INTEGER NOT NULL
            )
        """
        
        // Sessions table
        private const val CREATE_SESSIONS_TABLE = """
            CREATE TABLE sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nic TEXT NOT NULL,
                jwt TEXT NOT NULL,
                expires_at INTEGER NOT NULL,
                created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
            )
        """
        
        // Stations table
        private const val CREATE_STATIONS_TABLE = """
            CREATE TABLE stations (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                lat REAL NOT NULL,
                lng REAL NOT NULL,
                type TEXT,
                slots INTEGER NOT NULL DEFAULT 1,
                active INTEGER NOT NULL DEFAULT 1,
                updated_at INTEGER NOT NULL
            )
        """
        
        // Reservations table
        private const val CREATE_RESERVATIONS_TABLE = """
            CREATE TABLE reservations (
                id TEXT PRIMARY KEY,
                owner_nic TEXT NOT NULL,
                station_id TEXT NOT NULL,
                start_ts INTEGER NOT NULL,
                end_ts INTEGER NOT NULL,
                status TEXT NOT NULL,
                approved INTEGER NOT NULL DEFAULT 0,
                qr_payload TEXT,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
        """
        
        // Sync queue table
        private const val CREATE_SYNC_QUEUE_TABLE = """
            CREATE TABLE sync_queue (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                kind TEXT NOT NULL,
                payload TEXT NOT NULL,
                created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
                retries INTEGER NOT NULL DEFAULT 0
            )
        """
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USERS_TABLE)
        db.execSQL(CREATE_SESSIONS_TABLE)
        db.execSQL(CREATE_STATIONS_TABLE)
        db.execSQL(CREATE_RESERVATIONS_TABLE)
        db.execSQL(CREATE_SYNC_QUEUE_TABLE)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades here
        // For now, just recreate tables
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS sessions")
        db.execSQL("DROP TABLE IF EXISTS stations")
        db.execSQL("DROP TABLE IF EXISTS reservations")
        db.execSQL("DROP TABLE IF EXISTS sync_queue")
        onCreate(db)
    }
    
    // Helper methods for common operations
    fun clearAllData() {
        val db = writableDatabase
        db.execSQL("DELETE FROM users")
        db.execSQL("DELETE FROM sessions")
        db.execSQL("DELETE FROM stations")
        db.execSQL("DELETE FROM reservations")
        db.execSQL("DELETE FROM sync_queue")
        db.close()
    }
    
    fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
}
