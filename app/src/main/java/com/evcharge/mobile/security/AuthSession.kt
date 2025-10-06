package com.evcharge.mobile.security

import com.evcharge.mobile.data.local.DbHelper
import com.evcharge.mobile.data.local.dao.UserDao
import com.evcharge.mobile.data.local.model.LocalUser
import com.evcharge.mobile.util.Time

class AuthSession(
    private val tokenStore: TokenStore,
    private val dbHelper: DbHelper
) {
    
    private val userDao = UserDao(dbHelper)
    
    fun saveSession(
        jwt: String,
        expiresAt: Long,
        nic: String,
        role: String,
        name: String,
        email: String? = null,
        phone: String? = null
    ) {
        // Save to encrypted preferences
        tokenStore.saveToken(jwt, expiresAt, nic, role, name)
        
        // Save to local database
        val user = LocalUser(
            nic = nic,
            role = role,
            name = name,
            email = email,
            phone = phone,
            isActive = true,
            updatedAt = Time.now()
        )
        userDao.insertOrUpdate(user)
    }
    
    fun getCurrentUser(): LocalUser? {
        val nic = tokenStore.getNic()
        return if (!nic.isNullOrEmpty()) {
            userDao.getByNic(nic)
        } else {
            null
        }
    }
    
    fun isLoggedIn(): Boolean {
        return tokenStore.hasValidSession()
    }
    
    fun getToken(): String? {
        return if (tokenStore.isTokenValid()) {
            tokenStore.getToken()
        } else {
            null
        }
    }
    
    fun logout() {
        tokenStore.clear()
        // Optionally clear local user data
        // userDao.clearAll()
    }
    
    fun getRole(): String? {
        return tokenStore.getRole()
    }
    
    fun getNic(): String? {
        return tokenStore.getNic()
    }
    
    fun getName(): String? {
        return tokenStore.getName()
    }
}
