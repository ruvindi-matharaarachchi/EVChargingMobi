package com.evcharge.mobile.data.local.model

import android.database.Cursor

data class LocalUser(
    val nic: String,
    val role: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val isActive: Boolean,
    val updatedAt: Long
) {
    companion object {
        fun fromCursor(cursor: Cursor): LocalUser {
            return LocalUser(
                nic = cursor.getString(cursor.getColumnIndexOrThrow("nic")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")).takeIf { it.isNotEmpty() },
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")).takeIf { it.isNotEmpty() },
                isActive = cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1,
                updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))
            )
        }
    }
}
