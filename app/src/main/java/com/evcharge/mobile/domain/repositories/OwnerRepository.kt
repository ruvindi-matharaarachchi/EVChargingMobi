package com.evcharge.mobile.domain.repositories

import com.evcharge.mobile.data.remote.OwnerUpdateRequest
import com.evcharge.mobile.domain.entities.Owner
import com.evcharge.mobile.util.Result

interface OwnerRepository {
    suspend fun updateOwner(nic: String, request: OwnerUpdateRequest): Result<Owner>
    suspend fun deactivateOwner(nic: String): Result<String>
    fun getCurrentOwner(): Owner?
    fun cacheOwner(owner: Owner)
}
