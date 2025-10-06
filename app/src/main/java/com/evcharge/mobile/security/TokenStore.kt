package com.evcharge.mobile.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.evcharge.mobile.util.Time

class TokenStore(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_tokens",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    private companion object {
        private const val KEY_JWT = "jwt_token"
        private const val KEY_EXPIRES_AT = "expires_at"
        private const val KEY_NIC = "nic"
        private const val KEY_ROLE = "role"
        private const val KEY_NAME = "name"
    }
    
    fun saveToken(jwt: String, expiresAt: Long, nic: String, role: String, name: String) {
        sharedPreferences.edit()
            .putString(KEY_JWT, jwt)
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .putString(KEY_NIC, nic)
            .putString(KEY_ROLE, role)
            .putString(KEY_NAME, name)
            .apply()
    }
    
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_JWT, null)
    }
    
    fun getExpiresAt(): Long {
        return sharedPreferences.getLong(KEY_EXPIRES_AT, 0L)
    }
    
    fun getNic(): String? {
        return sharedPreferences.getString(KEY_NIC, null)
    }
    
    fun getRole(): String? {
        return sharedPreferences.getString(KEY_ROLE, null)
    }
    
    fun getName(): String? {
        return sharedPreferences.getString(KEY_NAME, null)
    }
    
    fun isTokenValid(): Boolean {
        val token = getToken()
        val expiresAt = getExpiresAt()
        return !token.isNullOrEmpty() && !Time.isExpired(expiresAt)
    }
    
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
    
    fun hasValidSession(): Boolean {
        return isTokenValid() && !getNic().isNullOrEmpty()
    }
}
