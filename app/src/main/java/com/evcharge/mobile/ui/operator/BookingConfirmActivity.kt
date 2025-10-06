package com.evcharge.mobile.ui.operator

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.evcharge.mobile.R
import com.evcharge.mobile.data.remote.BookingApi
import com.evcharge.mobile.data.remote.HttpClient
import com.evcharge.mobile.security.AuthSession
import com.evcharge.mobile.security.TokenStore
import com.evcharge.mobile.util.Result
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class BookingConfirmActivity : AppCompatActivity() {
    
    private lateinit var authSession: AuthSession
    private lateinit var bookingApi: BookingApi
    private lateinit var progressBar: ProgressBar
    private lateinit var tvBookingId: TextView
    private lateinit var tvCustomerName: TextView
    private lateinit var tvStationName: TextView
    private lateinit var tvTimeSlot: TextView
    private lateinit var chipStatus: Chip
    private lateinit var btnComplete: MaterialButton
    
    private var qrPayload: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirm)
        
        authSession = AuthSession(TokenStore(this), com.evcharge.mobile.App.instance.dbHelper)
        
        val httpClient = HttpClient()
        httpClient.setAuthToken(authSession.getToken())
        bookingApi = BookingApi(httpClient)
        
        setupViews()
        
        // Get QR payload from intent
        qrPayload = intent.getStringExtra("qr_payload")
        if (qrPayload != null) {
            loadBookingDetails()
        } else {
            Toast.makeText(this, "No QR payload provided", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupViews() {
        progressBar = findViewById(R.id.progress_bar)
        tvBookingId = findViewById(R.id.tv_booking_id)
        tvCustomerName = findViewById(R.id.tv_customer_name)
        tvStationName = findViewById(R.id.tv_station_name)
        tvTimeSlot = findViewById(R.id.tv_time_slot)
        chipStatus = findViewById(R.id.chip_status)
        btnComplete = findViewById(R.id.btn_complete)
        
        btnComplete.setOnClickListener {
            completeBooking()
        }
        
        findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
            finish()
        }
    }
    
    private fun loadBookingDetails() {
        // TODO: Load booking details from QR payload
        // For now, show placeholder data
        tvBookingId.text = "Booking ID: #12345"
        tvCustomerName.text = "Customer: John Doe"
        tvStationName.text = "Station: Station A"
        tvTimeSlot.text = "Time: Dec 15, 2023 10:00 - 12:00"
        chipStatus.text = "Approved"
    }
    
    private fun completeBooking() {
        if (qrPayload.isNullOrEmpty()) {
            Toast.makeText(this, "No QR payload available", Toast.LENGTH_SHORT).show()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            val result = bookingApi.completeBooking(qrPayload!!)
            
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this@BookingConfirmActivity, "Booking completed successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this@BookingConfirmActivity, "Failed to complete booking: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
            showLoading(false)
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnComplete.isEnabled = !show
    }
}
