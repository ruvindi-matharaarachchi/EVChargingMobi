package com.evcharge.mobile.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.evcharge.mobile.data.local.DbHelper
import com.evcharge.mobile.data.local.model.LocalUser

class UserDao(private val dbHelper: DbHelper) {
    
    fun insertOrUpdate(user: LocalUser): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nic", user.nic)
            put("role", user.role)
            put("name", user.name)
            put("email", user.email ?: "")
            put("phone", user.phone ?: "")
            put("is_active", if (user.isActive) 1 else 0)
            put("updated_at", user.updatedAt)
        }
        
        val result = db.insertWithOnConflict(
            "users", null, values, 
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
        return result != -1L
    }
    
    fun getByNic(nic: String): LocalUser? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "users",
            null,
            "nic = ?",
            arrayOf(nic),
            null, null, null
        )
        
        return if (cursor.moveToFirst()) {
            val user = LocalUser.fromCursor(cursor)
            cursor.close()
            db.close()
            user
        } else {
            cursor.close()
            db.close()
            null
        }
    }
    
    fun getAll(): List<LocalUser> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "users",
            null, null, null, null, null, "updated_at DESC"
        )
        
        val users = mutableListOf<LocalUser>()
        while (cursor.moveToNext()) {
            users.add(LocalUser.fromCursor(cursor))
        }
        
        cursor.close()
        db.close()
        return users
    }
    
    fun deleteByNic(nic: String): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("users", "nic = ?", arrayOf(nic))
        db.close()
        return result > 0
    }
    
    fun clearAll(): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("users", null, null)
        db.close()
        return result >= 0
    }
}
