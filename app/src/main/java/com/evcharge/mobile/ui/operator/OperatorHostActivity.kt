package com.evcharge.mobile.ui.operator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.evcharge.mobile.R
import com.evcharge.mobile.security.AuthSession
import com.evcharge.mobile.security.TokenStore
import com.evcharge.mobile.ui.auth.AuthActivity
import com.google.android.material.button.MaterialButton

class OperatorHostActivity : AppCompatActivity() {
    
    private lateinit var authSession: AuthSession
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_host)
        
        authSession = AuthSession(TokenStore(this), com.evcharge.mobile.App.instance.dbHelper)
        
        // Check if logged in
        if (!authSession.isLoggedIn()) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }
        
        setupViews()
    }
    
    private fun setupViews() {
        val btnScanQr = findViewById<MaterialButton>(R.id.btn_scan_qr)
        val btnCompleteBooking = findViewById<MaterialButton>(R.id.btn_complete_booking)
        
        btnScanQr.setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }
        
        btnCompleteBooking.setOnClickListener {
            startActivity(Intent(this, BookingConfirmActivity::class.java))
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.operator_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                authSession.logout()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
