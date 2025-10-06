package com.evcharge.mobile.ui.owner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evcharge.mobile.R
import com.evcharge.mobile.data.remote.BookingApi
import com.evcharge.mobile.data.remote.HttpClient
import com.evcharge.mobile.security.AuthSession
import com.evcharge.mobile.security.TokenStore
import com.evcharge.mobile.util.Result
import kotlinx.coroutines.launch

class BookingsListFragment : Fragment() {
    
    private lateinit var authSession: AuthSession
    private lateinit var bookingApi: BookingApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: BookingsAdapter
    
    private var isUpcoming: Boolean = true
    
    companion object {
        fun newInstance(isUpcoming: Boolean): BookingsListFragment {
            val fragment = BookingsListFragment()
            val args = Bundle()
            args.putBoolean("isUpcoming", isUpcoming)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookings_list, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        isUpcoming = arguments?.getBoolean("isUpcoming", true) ?: true
        
        authSession = AuthSession(TokenStore(requireContext()), com.evcharge.mobile.App.instance.dbHelper)
        
        val httpClient = HttpClient()
        httpClient.setAuthToken(authSession.getToken())
        bookingApi = BookingApi(httpClient)
        
        recyclerView = view.findViewById(R.id.recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        tvEmpty = view.findViewById(R.id.tv_empty)
        
        setupRecyclerView()
        loadBookings()
    }
    
    private fun setupRecyclerView() {
        adapter = BookingsAdapter { booking ->
            // TODO: Handle booking item click
        }
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
    
    private fun loadBookings() {
        val nic = authSession.getNic()
        if (nic.isNullOrEmpty()) {
            showEmpty()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            val result = bookingApi.getBookingsByOwner(nic)
            
            when (result) {
                is Result.Success -> {
                    val bookings = result.data
                    val currentTime = System.currentTimeMillis()
                    val filteredBookings = if (isUpcoming) {
                        bookings.filter { com.evcharge.mobile.util.Time.parseApiTimestamp(it.startTime) > currentTime }
                    } else {
                        bookings.filter { com.evcharge.mobile.util.Time.parseApiTimestamp(it.startTime) <= currentTime }
                    }
                    
                    if (filteredBookings.isEmpty()) {
                        showEmpty()
                    } else {
                        adapter.submitList(filteredBookings)
                        showContent()
                    }
                }
                is Result.Error -> {
                    showEmpty()
                }
            }
            
            showLoading(false)
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun showContent() {
        recyclerView.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
    }
    
    private fun showEmpty() {
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
    }
}
