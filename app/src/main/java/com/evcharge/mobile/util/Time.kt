package com.evcharge.mobile.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Time {
    
    fun now(): Long = System.currentTimeMillis()
    
    fun formatTimestamp(timestamp: Long, pattern: String = "MMM dd, yyyy HH:mm"): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern))
    }
    
    fun parseApiTimestamp(apiTimestamp: String): Long {
        return Instant.parse(apiTimestamp).toEpochMilli()
    }
    
    fun toApiTimestamp(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp).toString()
    }
    
    fun isExpired(expiresAt: Long): Boolean {
        return now() >= expiresAt
    }
    
    fun getTimeUntilExpiry(expiresAt: Long): Long {
        return expiresAt - now()
    }
}
