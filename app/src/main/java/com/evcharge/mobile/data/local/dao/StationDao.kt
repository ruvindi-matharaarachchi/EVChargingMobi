package com.evcharge.mobile.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.evcharge.mobile.data.local.DbHelper
import com.evcharge.mobile.data.local.model.StationLocal

class StationDao(private val dbHelper: DbHelper) {
    
    fun insertOrUpdate(station: StationLocal): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", station.id)
            put("name", station.name)
            put("lat", station.lat)
            put("lng", station.lng)
            put("type", station.type ?: "")
            put("slots", station.slots)
            put("active", if (station.active) 1 else 0)
            put("updated_at", station.updatedAt)
        }
        
        val result = db.insertWithOnConflict(
            "stations", null, values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
        return result != -1L
    }
    
    fun getById(id: String): StationLocal? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "stations",
            null,
            "id = ?",
            arrayOf(id),
            null, null, null
        )
        
        return if (cursor.moveToFirst()) {
            val station = StationLocal.fromCursor(cursor)
            cursor.close()
            db.close()
            station
        } else {
            cursor.close()
            db.close()
            null
        }
    }
    
    fun getAll(): List<StationLocal> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "stations",
            null, null, null, null, null, "updated_at DESC"
        )
        
        val stations = mutableListOf<StationLocal>()
        while (cursor.moveToNext()) {
            stations.add(StationLocal.fromCursor(cursor))
        }
        
        cursor.close()
        db.close()
        return stations
    }
    
    fun getActive(): List<StationLocal> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "stations",
            null,
            "active = 1",
            null, null, null, "updated_at DESC"
        )
        
        val stations = mutableListOf<StationLocal>()
        while (cursor.moveToNext()) {
            stations.add(StationLocal.fromCursor(cursor))
        }
        
        cursor.close()
        db.close()
        return stations
    }
    
    fun deleteById(id: String): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("stations", "id = ?", arrayOf(id))
        db.close()
        return result > 0
    }
    
    fun clearAll(): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("stations", null, null)
        db.close()
        return result >= 0
    }
}
