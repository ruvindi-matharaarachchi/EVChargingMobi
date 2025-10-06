package com.evcharge.mobile.data.local.model

import android.database.Cursor

data class StationLocal(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val type: String?,
    val slots: Int,
    val active: Boolean,
    val updatedAt: Long
) {
    companion object {
        fun fromCursor(cursor: Cursor): StationLocal {
            return StationLocal(
                id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat")),
                lng = cursor.getDouble(cursor.getColumnIndexOrThrow("lng")),
                type = cursor.getString(cursor.getColumnIndexOrThrow("type")).takeIf { it.isNotEmpty() },
                slots = cursor.getInt(cursor.getColumnIndexOrThrow("slots")),
                active = cursor.getInt(cursor.getColumnIndexOrThrow("active")) == 1,
                updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))
            )
        }
    }
}
