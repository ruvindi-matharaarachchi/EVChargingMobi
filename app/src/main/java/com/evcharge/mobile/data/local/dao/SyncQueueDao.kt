package com.evcharge.mobile.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.evcharge.mobile.data.local.DbHelper
import com.evcharge.mobile.data.local.model.SyncItem

class SyncQueueDao(private val dbHelper: DbHelper) {
    
    fun insert(kind: String, payload: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("kind", kind)
            put("payload", payload)
            put("created_at", System.currentTimeMillis())
            put("retries", 0)
        }
        
        val result = db.insert("sync_queue", null, values)
        db.close()
        return result
    }
    
    fun getAll(): List<SyncItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "sync_queue",
            null, null, null, null, null, "created_at ASC"
        )
        
        val items = mutableListOf<SyncItem>()
        while (cursor.moveToNext()) {
            items.add(SyncItem.fromCursor(cursor))
        }
        
        cursor.close()
        db.close()
        return items
    }
    
    fun getPending(): List<SyncItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "sync_queue",
            null,
            "retries < 3",
            null, null, null, "created_at ASC"
        )
        
        val items = mutableListOf<SyncItem>()
        while (cursor.moveToNext()) {
            items.add(SyncItem.fromCursor(cursor))
        }
        
        cursor.close()
        db.close()
        return items
    }
    
    fun incrementRetries(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("retries", "retries + 1")
        }
        val result = db.update("sync_queue", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
    
    fun deleteById(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("sync_queue", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
    
    fun clearAll(): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("sync_queue", null, null)
        db.close()
        return result >= 0
    }
}
