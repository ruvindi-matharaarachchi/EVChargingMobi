package com.evcharge.mobile.domain.entities

data class Owner(
    val nic: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val isActive: Boolean
)
