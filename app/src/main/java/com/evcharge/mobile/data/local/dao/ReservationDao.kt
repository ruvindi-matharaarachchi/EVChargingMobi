package com.evcharge.mobile.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.evcharge.mobile.data.local.DbHelper
import com.evcharge.mobile.data.local.model.ReservationLocal

class ReservationDao(private val dbHelper: DbHelper) {
    
    fun insertOrUpdate(reservation: ReservationLocal): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", reservation.id)
            put("owner_nic", reservation.ownerNic)
            put("station_id", reservation.stationId)
            put("start_ts", reservation.startTs)
            put("end_ts", reservation.endTs)
            put("status", reservation.status)
            put("approved", if (reservation.approved) 1 else 0)
            put("qr_payload", reservation.qrPayload ?: "")
            put("created_at", reservation.createdAt)
            put("updated_at", reservation.updatedAt)
        }
        
        val result = db.insertWithOnConflict(
            "reservations", null, values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
        return result != -1L
    }
    
    fun getById(id: String): ReservationLocal? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "reservations",
            null,
            "id = ?",
            arrayOf(id),
            null, null, null
        )
        
        return if (cursor.moveToFirst()) {
            val reservation = ReservationLocal.fromCursor(cursor)
            cursor.close()
            db.close()
            reservation
        } else {
            cursor.close()
            db.close()
            null
        }
    }
    
    fun getByOwnerNic(ownerNic: String): List<ReservationLocal> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "reservations",
            null,
            "owner_nic = ?",
            arrayOf(ownerNic),
            null, null, "start_ts DESC"
        )
        
        val reservations = mutableListOf<ReservationLocal>()
        while (cursor.moveToNext()) {
            reservations.add(ReservationLocal.fromCursor(cursor))
        }
        
        cursor.close()
        db.close()
        return reservations
    }
    
    fun getUpcomingByOwnerNic(ownerNic: String): List<ReservationLocal> {
        val currentTime = System.currentTimeMillis()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "reservations",
            null,
            "owner_nic = ? AND start_ts > ?",
            arrayOf(ownerNic, currentTime.toString()),
            null, null, "start_ts ASC"
        )
        
        val reservations = mutableListOf<ReservationLocal>()
        while (cursor.moveToNext()) {
            reservations.add(ReservationLocal.fromCursor(cursor))
        }
        
        cursor.close()
        db.close()
        return reservations
    }
    
    fun getHistoryByOwnerNic(ownerNic: String): List<ReservationLocal> {
        val currentTime = System.currentTimeMillis()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "reservations",
            null,
            "owner_nic = ? AND start_ts <= ?",
            arrayOf(ownerNic, currentTime.toString()),
            null, null, "start_ts DESC"
        )
        
        val reservations = mutableListOf<ReservationLocal>()
        while (cursor.moveToNext()) {
            reservations.add(ReservationLocal.fromCursor(cursor))
        }
        
        cursor.close()
        db.close()
        return reservations
    }
    
    fun deleteById(id: String): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("reservations", "id = ?", arrayOf(id))
        db.close()
        return result > 0
    }
    
    fun clearAll(): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("reservations", null, null)
        db.close()
        return result >= 0
    }
}
