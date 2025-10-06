package com.evcharge.mobile.ui.owner.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.evcharge.mobile.R
import com.evcharge.mobile.data.remote.BookingApi
import com.evcharge.mobile.data.remote.HttpClient
import com.evcharge.mobile.security.AuthSession
import com.evcharge.mobile.security.TokenStore
import com.evcharge.mobile.ui.owner.ReservationActivity
import com.evcharge.mobile.util.Result
import kotlinx.coroutines.launch

class OwnerHomeFragment : Fragment() {
    
    private lateinit var authSession: AuthSession
    private lateinit var bookingApi: BookingApi
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPendingCount: TextView
    private lateinit var tvApprovedCount: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_owner_home, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        authSession = AuthSession(TokenStore(requireContext()), com.evcharge.mobile.App.instance.dbHelper)
        
        val httpClient = HttpClient()
        httpClient.setAuthToken(authSession.getToken())
        bookingApi = BookingApi(httpClient)
        
        progressBar = view.findViewById(R.id.progress_bar)
        tvPendingCount = view.findViewById(R.id.tv_pending_count)
        tvApprovedCount = view.findViewById(R.id.tv_approved_count)
        
        setupViews(view)
        loadDashboardData()
    }
    
    private fun setupViews(view: View) {
        val btnCreateReservation = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_create_reservation)
        val btnNearbyStations = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_nearby_stations)
        
        btnCreateReservation.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), ReservationActivity::class.java))
        }
        
        btnNearbyStations.setOnClickListener {
            if (checkLocationPermission()) {
                // TODO: Load map with nearby stations
                Toast.makeText(requireContext(), "Map functionality will be implemented", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadDashboardData() {
        val nic = authSession.getNic()
        if (nic.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            val result = bookingApi.getDashboardCounts(nic)
            
            when (result) {
                is Result.Success -> {
                    val counts = result.data
                    tvPendingCount.text = counts.pending.toString()
                    tvApprovedCount.text = counts.approved.toString()
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Failed to load dashboard: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
            showLoading(false)
        }
    }
    
    private fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            false
        } else {
            true
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load map
                Toast.makeText(requireContext(), "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
