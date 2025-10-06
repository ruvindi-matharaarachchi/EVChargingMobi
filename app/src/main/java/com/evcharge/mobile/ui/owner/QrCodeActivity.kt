package com.evcharge.mobile.ui.owner

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
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
import com.evcharge.mobile.util.Time
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class QrCodeActivity : AppCompatActivity() {
    
    private lateinit var authSession: AuthSession
    private lateinit var bookingApi: BookingApi
    private lateinit var ivQrCode: ImageView
    private lateinit var tvBookingId: TextView
    private lateinit var tvStationName: TextView
    private lateinit var tvTimeSlot: TextView
    
    private var bookingId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
        
        authSession = AuthSession(TokenStore(this), com.evcharge.mobile.App.instance.dbHelper)
        
        val httpClient = HttpClient()
        httpClient.setAuthToken(authSession.getToken())
        bookingApi = BookingApi(httpClient)
        
        setupViews()
        
        // Get booking ID from intent
        bookingId = intent.getStringExtra("booking_id")
        if (bookingId != null) {
            loadBookingDetails()
        } else {
            Toast.makeText(this, "No booking ID provided", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupViews() {
        ivQrCode = findViewById(R.id.iv_qr_code)
        tvBookingId = findViewById(R.id.tv_booking_id)
        tvStationName = findViewById(R.id.tv_station_name)
        tvTimeSlot = findViewById(R.id.tv_time_slot)
        
        findViewById<MaterialButton>(R.id.btn_share).setOnClickListener {
            shareQrCode()
        }
        
        findViewById<MaterialButton>(R.id.btn_close).setOnClickListener {
            finish()
        }
    }
    
    private fun loadBookingDetails() {
        if (bookingId.isNullOrEmpty()) return
        
        lifecycleScope.launch {
            val result = bookingApi.getBookingById(bookingId!!)
            
            when (result) {
                is Result.Success -> {
                    val booking = result.data
                    displayBookingDetails(booking)
                    generateQrCode(booking.qrPayload ?: "")
                }
                is Result.Error -> {
                    Toast.makeText(this@QrCodeActivity, "Failed to load booking: ${result.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
    
    private fun displayBookingDetails(booking: com.evcharge.mobile.data.remote.BookingResponse) {
        tvBookingId.text = "Booking ID: #${booking.id}"
        tvStationName.text = "Station: Station ${booking.stationId}" // TODO: Get actual station name
        tvTimeSlot.text = "Time: ${Time.formatTimestamp(Time.parseApiTimestamp(booking.startTime))} - ${Time.formatTimestamp(Time.parseApiTimestamp(booking.endTime))}"
    }
    
    private fun generateQrCode(qrPayload: String) {
        if (qrPayload.isEmpty()) {
            Toast.makeText(this, "No QR payload available", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            // TODO: Implement ZXing QR code generation
            // For now, show placeholder
            Toast.makeText(this, "QR Code generation will be implemented with ZXing", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to generate QR code: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun shareQrCode() {
        // TODO: Implement sharing functionality
        Toast.makeText(this, "Share functionality will be implemented", Toast.LENGTH_SHORT).show()
    }
}
