package com.evcharge.mobile.data.local.model

import android.database.Cursor

data class ReservationLocal(
    val id: String,
    val ownerNic: String,
    val stationId: String,
    val startTs: Long,
    val endTs: Long,
    val status: String,
    val approved: Boolean,
    val qrPayload: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromCursor(cursor: Cursor): ReservationLocal {
            return ReservationLocal(
                id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                ownerNic = cursor.getString(cursor.getColumnIndexOrThrow("owner_nic")),
                stationId = cursor.getString(cursor.getColumnIndexOrThrow("station_id")),
                startTs = cursor.getLong(cursor.getColumnIndexOrThrow("start_ts")),
                endTs = cursor.getLong(cursor.getColumnIndexOrThrow("end_ts")),
                status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                approved = cursor.getInt(cursor.getColumnIndexOrThrow("approved")) == 1,
                qrPayload = cursor.getString(cursor.getColumnIndexOrThrow("qr_payload")).takeIf { it.isNotEmpty() },
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))
            )
        }
    }
}
