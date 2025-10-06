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
        
        val httpClient = HttpClient()
        authApi = AuthApi(httpClient)
        authSession = AuthSession(TokenStore(this), com.evcharge.mobile.App.instance.dbHelper)
        
        // Check if already logged in
        if (authSession.isLoggedIn()) {
            navigateToMainActivity()
            return
        }
        
        setupViews()
    }
    
    private fun setupViews() {
        val etNic = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_nic)
        val etPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_password)
        val btnLogin = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_login)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvError = findViewById<TextView>(R.id.tv_error)
        
        btnLogin.setOnClickListener {
            val nic = etNic.text?.toString()?.trim()
            val password = etPassword.text?.toString()
            
            if (nic.isNullOrEmpty() || password.isNullOrEmpty()) {
                showError("Please enter both NIC and password")
                return@setOnClickListener
            }
            
            login(nic, password)
        }
    }
    
    private fun login(nic: String, password: String) {
        showLoading(true)
        hideError()
        
        lifecycleScope.launch {
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
        }
    }
    
    private fun navigateToMainActivity() {
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
    }
    
    private fun showLoading(show: Boolean) {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val btnLogin = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_login)
        
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
    }
    
    private fun showError(message: String) {
        val tvError = findViewById<TextView>(R.id.tv_error)
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }
    
    private fun hideError() {
        val tvError = findViewById<TextView>(R.id.tv_error)
        tvError.visibility = View.GONE
    }
}
