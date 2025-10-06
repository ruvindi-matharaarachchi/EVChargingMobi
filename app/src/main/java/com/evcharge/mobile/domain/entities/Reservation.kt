package com.evcharge.mobile.domain.entities

data class Reservation(
    val id: String,
    val ownerNic: String,
    val stationId: String,
    val startTime: Long,
    val endTime: Long,
    val status: String,
    val isApproved: Boolean,
    val qrPayload: String?,
    val createdAt: Long,
    val updatedAt: Long
)
