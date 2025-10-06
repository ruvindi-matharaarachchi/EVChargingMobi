package com.evcharge.mobile.domain.entities

data class Station(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: String?,
    val slots: Int,
    val isActive: Boolean
)
