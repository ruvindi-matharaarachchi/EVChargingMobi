package com.evcharge.mobile.ui.owner

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.evcharge.mobile.R
import com.evcharge.mobile.data.remote.BookingApi
import com.evcharge.mobile.data.remote.BookingRequest
import com.evcharge.mobile.data.remote.HttpClient
import com.evcharge.mobile.data.remote.StationApi
import com.evcharge.mobile.security.AuthSession
import com.evcharge.mobile.security.TokenStore
import com.evcharge.mobile.util.DateTimeRules
import com.evcharge.mobile.util.Result
import com.evcharge.mobile.util.Time
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.util.*

class ReservationActivity : AppCompatActivity() {
    
    private lateinit var authSession: AuthSession
    private lateinit var stationApi: StationApi
    private lateinit var bookingApi: BookingApi
    private lateinit var progressBar: ProgressBar
    private lateinit var etStation: TextInputEditText
    private lateinit var etStartTime: TextInputEditText
    private lateinit var etEndTime: TextInputEditText
    private lateinit var cardSummary: MaterialCardView
    private lateinit var tvSummaryStation: android.widget.TextView
    private lateinit var tvSummaryTime: android.widget.TextView
    private lateinit var btnConfirm: MaterialButton
    
    private var selectedStationId: String? = null
    private var selectedStartTime: Long? = null
    private var selectedEndTime: Long? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)
        
        authSession = AuthSession(TokenStore(this), com.evcharge.mobile.App.instance.dbHelper)
        
        val httpClient = HttpClient()
        httpClient.setAuthToken(authSession.getToken())
        stationApi = StationApi(httpClient)
        bookingApi = BookingApi(httpClient)
        
        setupViews()
        loadStations()
    }
    
    private fun setupViews() {
        etStation = findViewById(R.id.et_station)
        etStartTime = findViewById(R.id.et_start_time)
        etEndTime = findViewById(R.id.et_end_time)
        cardSummary = findViewById(R.id.card_summary)
        tvSummaryStation = findViewById(R.id.tv_summary_station)
        tvSummaryTime = findViewById(R.id.tv_summary_time)
        btnConfirm = findViewById(R.id.btn_confirm)
        progressBar = findViewById(R.id.progress_bar)
        
        etStation.setOnClickListener {
            showStationSelection()
        }
        
        etStartTime.setOnClickListener {
            showDateTimePicker(true)
        }
        
        etEndTime.setOnClickListener {
            showDateTimePicker(false)
        }
        
        btnConfirm.setOnClickListener {
            createReservation()
        }
        
        findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
            finish()
        }
    }
    
    private fun loadStations() {
        showLoading(true)
        
        lifecycleScope.launch {
            val result = stationApi.getAllStations()
            
            when (result) {
                is Result.Success -> {
                    // TODO: Populate station dropdown
                    showLoading(false)
                }
                is Result.Error -> {
                    Toast.makeText(this@ReservationActivity, "Failed to load stations: ${result.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }
    
    private fun showStationSelection() {
        // TODO: Show station selection dialog
        Toast.makeText(this, "Station selection will be implemented", Toast.LENGTH_SHORT).show()
    }
    
    private fun showDateTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.set(year, month, dayOfMonth, hourOfDay, minute)
                        val selectedTime = selectedCalendar.timeInMillis
                        
                        if (isStartTime) {
                            selectedStartTime = selectedTime
                            etStartTime.setText(DateTimeRules.formatDateTime(selectedTime))
                        } else {
                            selectedEndTime = selectedTime
                            etEndTime.setText(DateTimeRules.formatDateTime(selectedTime))
                        }
                        
                        updateSummary()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Set minimum date to today
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        // Set maximum date to 7 days from now
        datePickerDialog.datePicker.maxDate = DateTimeRules.getMaxStartTime()
        
        datePickerDialog.show()
    }
    
    private fun updateSummary() {
        if (selectedStationId != null && selectedStartTime != null && selectedEndTime != null) {
            tvSummaryStation.text = "Station: Station $selectedStationId"
            tvSummaryTime.text = "Time: ${DateTimeRules.formatDateTime(selectedStartTime!!)} - ${DateTimeRules.formatDateTime(selectedEndTime!!)}"
            cardSummary.visibility = View.VISIBLE
            btnConfirm.isEnabled = true
        } else {
            cardSummary.visibility = View.GONE
            btnConfirm.isEnabled = false
        }
    }
    
    private fun createReservation() {
        if (selectedStationId == null || selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validate business rules
        if (!DateTimeRules.isWithinSevenDayWindow(selectedStartTime!!)) {
            Toast.makeText(this, "Reservation must be within 7 days", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (selectedEndTime!! <= selectedStartTime!!) {
            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            val request = BookingRequest(
                stationId = selectedStationId!!,
                startTime = Time.toApiTimestamp(selectedStartTime!!),
                endTime = Time.toApiTimestamp(selectedEndTime!!)
            )
            
            val result = bookingApi.createBooking(request)
            
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this@ReservationActivity, "Reservation created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this@ReservationActivity, "Failed to create reservation: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
            showLoading(false)
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnConfirm.isEnabled = !show
    }
}
