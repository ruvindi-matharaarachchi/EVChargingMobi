package com.evcharge.mobile.ui.owner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.evcharge.mobile.R
import com.google.android.material.tabs.TabLayoutMediator

class MyBookingsFragment : Fragment() {
    
    private lateinit var viewPager: androidx.viewpager2.widget.ViewPager2
    private lateinit var tabLayout: com.google.android.material.tabs.TabLayout
    private lateinit var progressBar: ProgressBar
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_bookings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)
        progressBar = view.findViewById(R.id.progress_bar)
        
        setupViewPager()
    }
    
    private fun setupViewPager() {
        val adapter = BookingsPagerAdapter(this)
        viewPager.adapter = adapter
        
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.upcoming_bookings)
                1 -> getString(R.string.booking_history)
                else -> ""
            }
        }.attach()
    }
    
    private inner class BookingsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BookingsListFragment.newInstance(true) // Upcoming
                1 -> BookingsListFragment.newInstance(false) // History
                else -> BookingsListFragment.newInstance(true)
            }
        }
    }
}
