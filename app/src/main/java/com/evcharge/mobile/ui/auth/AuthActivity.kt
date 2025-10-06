package com.evcharge.mobile.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.evcharge.mobile.R
import com.evcharge.mobile.data.remote.AuthApi
import com.evcharge.mobile.data.remote.HttpClient
import com.evcharge.mobile.security.AuthSession
import com.evcharge.mobile.security.TokenStore
import com.evcharge.mobile.ui.operator.OperatorHostActivity
import com.evcharge.mobile.ui.owner.OwnerHostActivity
import com.evcharge.mobile.util.Result
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {
    
    private lateinit var authApi: AuthApi
    private lateinit var authSession: AuthSession
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        
        try {
            val httpClient = HttpClient()
            authApi = AuthApi(httpClient)
            authSession = AuthSession(TokenStore(this), com.evcharge.mobile.App.instance.dbHelper)
            
            // Check if already logged in
            if (authSession.isLoggedIn()) {
                navigateToMainActivity()
                return
            }
            
            setupViews()
            
            // Check backend connectivity
            checkBackendConnectivity()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "App initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupViews() {
        try {
            val etNic = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_nic)
            val etPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_password)
            val btnLogin = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_login)
            val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
            val tvError = findViewById<TextView>(R.id.tv_error)
            
            if (etNic == null || etPassword == null || btnLogin == null) {
                Toast.makeText(this, "UI elements not found", Toast.LENGTH_LONG).show()
                return
            }
            
            btnLogin.setOnClickListener {
                val nic = etNic.text?.toString()?.trim()
                val password = etPassword.text?.toString()
                
                if (nic.isNullOrEmpty() || password.isNullOrEmpty()) {
                    showError("Please enter both NIC and password")
                    return@setOnClickListener
                }
                
                login(nic, password)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "UI setup failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear references to prevent memory leaks
        // Note: authApi and authSession are lateinit, so we can't set them to null
        // Instead, we'll rely on garbage collection
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        // Clear caches when memory is low
        System.gc()
    }
    
    private fun login(nic: String, password: String) {
        showLoading(true)
        hideError()
        
        lifecycleScope.launch {
            try {
                val result = authApi.login(nic, password)
                
                when (result) {
                    is Result.Success -> {
                        val response = result.data
                        authSession.saveSession(
                            jwt = response.token,
                            expiresAt = com.evcharge.mobile.util.Time.parseApiTimestamp(response.expiresAt),
                            nic = response.nic,
                            role = response.role,
                            name = response.name
                        )
                        
                        showLoading(false)
                        navigateToMainActivity()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showError(result.message)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showLoading(false)
                showError("Login failed: ${e.message}")
            }
        }
    }
    
    private fun navigateToMainActivity() {
        try {
            val role = authSession.getRole()
            val intent = when (role) {
                "EVOwner" -> Intent(this, OwnerHostActivity::class.java)
                "StationOperator" -> Intent(this, OperatorHostActivity::class.java)
                else -> {
                    Toast.makeText(this, "Invalid role: $role", Toast.LENGTH_LONG).show()
                    return
                }
            }
            
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Navigation failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showLoading(show: Boolean) {
        try {
            val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
            val btnLogin = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_login)
            
            progressBar?.visibility = if (show) View.VISIBLE else View.GONE
            btnLogin?.isEnabled = !show
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showError(message: String) {
        try {
            val tvError = findViewById<TextView>(R.id.tv_error)
            tvError?.text = message
            tvError?.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
    
    private fun hideError() {
        try {
            val tvError = findViewById<TextView>(R.id.tv_error)
            tvError?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun checkBackendConnectivity() {
        lifecycleScope.launch {
            try {
                // First check if network is available
                if (!com.evcharge.mobile.App.instance.isNetworkAvailable()) {
                    Toast.makeText(this@AuthActivity, "📡 No internet connection. Please check your network.", Toast.LENGTH_LONG).show()
                    return@launch
                }
                
                // Test backend connectivity with health check
                val httpClient = HttpClient()
                val result = httpClient.checkBackendHealth()
                
                when (result) {
                    is Result.Success -> {
                        Toast.makeText(this@AuthActivity, "✅ Backend connected successfully!", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        when {
                            result.message.contains("Backend server is not running") -> {
                                Toast.makeText(this@AuthActivity, "❌ Backend server is not running. Please start the .NET API server.", Toast.LENGTH_LONG).show()
                            }
                            result.message.contains("Connection timeout") -> {
                                Toast.makeText(this@AuthActivity, "⏱️ Connection timeout. Check if backend server is running.", Toast.LENGTH_LONG).show()
                            }
                            result.message.contains("Cannot reach backend server") -> {
                                Toast.makeText(this@AuthActivity, "🌐 Cannot reach backend server. Check network connection.", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText(this@AuthActivity, "⚠️ Backend connection issue: ${result.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@AuthActivity, "❌ Failed to check backend connectivity: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
