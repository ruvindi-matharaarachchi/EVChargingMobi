package com.evcharge.mobile.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeRules {
    
    private const val SEVEN_DAYS_IN_MILLIS = 7 * 24 * 60 * 60 * 1000L
    private const val TWELVE_HOURS_IN_MILLIS = 12 * 60 * 60 * 1000L
    
    /**
     * Check if a reservation start time is within the 7-day window
     * @param startTimeMillis The start time in milliseconds
     * @return true if within 7 days, false otherwise
     */
    fun isWithinSevenDayWindow(startTimeMillis: Long): Boolean {
        val now = System.currentTimeMillis()
        val sevenDaysFromNow = now + SEVEN_DAYS_IN_MILLIS
        return startTimeMillis >= now && startTimeMillis <= sevenDaysFromNow
    }
    
    /**
     * Check if a reservation can be modified or cancelled (12-hour rule)
     * @param startTimeMillis The start time in milliseconds
     * @return true if can be modified/cancelled, false otherwise
     */
    fun canModifyOrCancel(startTimeMillis: Long): Boolean {
        val now = System.currentTimeMillis()
        val twelveHoursFromNow = now + TWELVE_HOURS_IN_MILLIS
        return startTimeMillis >= twelveHoursFromNow
    }
    
    /**
     * Get the minimum allowed start time (now)
     */
    fun getMinStartTime(): Long = System.currentTimeMillis()
    
    /**
     * Get the maximum allowed start time (7 days from now)
     */
    fun getMaxStartTime(): Long = System.currentTimeMillis() + SEVEN_DAYS_IN_MILLIS
    
    /**
     * Get the cutoff time for modifications (12 hours from now)
     */
    fun getModificationCutoffTime(): Long = System.currentTimeMillis() + TWELVE_HOURS_IN_MILLIS
    
    /**
     * Format a timestamp for display
     */
    fun formatDateTime(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    }
    
    /**
     * Format a timestamp for API requests (ISO format)
     */
    fun formatForApi(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        return instant.toString()
    }
    
    /**
     * Parse an API timestamp to milliseconds
     */
    fun parseFromApi(apiTimestamp: String): Long {
        return Instant.parse(apiTimestamp).toEpochMilli()
    }
}
