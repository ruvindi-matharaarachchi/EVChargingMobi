package com.evcharge.mobile.data.local.model

import android.database.Cursor

data class SyncItem(
    val id: Long,
    val kind: String,
    val payload: String,
    val createdAt: Long,
    val retries: Int
) {
    companion object {
        fun fromCursor(cursor: Cursor): SyncItem {
            return SyncItem(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                kind = cursor.getString(cursor.getColumnIndexOrThrow("kind")),
                payload = cursor.getString(cursor.getColumnIndexOrThrow("payload")),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                retries = cursor.getInt(cursor.getColumnIndexOrThrow("retries"))
            )
        }
    }
}
