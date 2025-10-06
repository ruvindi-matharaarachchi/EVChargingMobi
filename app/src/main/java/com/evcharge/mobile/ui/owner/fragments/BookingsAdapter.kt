package com.evcharge.mobile.ui.owner.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evcharge.mobile.R
import com.evcharge.mobile.data.remote.BookingResponse
import com.evcharge.mobile.util.DateTimeRules
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

class BookingsAdapter(
    private val onItemClick: (BookingResponse) -> Unit
) : ListAdapter<BookingResponse, BookingsAdapter.BookingViewHolder>(BookingDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStationName: TextView = itemView.findViewById(R.id.tv_station_name)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        private val chipStatus: Chip = itemView.findViewById(R.id.chip_status)
        private val btnViewQr: MaterialButton = itemView.findViewById(R.id.btn_view_qr)
        private val btnModify: MaterialButton = itemView.findViewById(R.id.btn_modify)
        
        fun bind(booking: BookingResponse) {
            tvStationName.text = "Station ${booking.stationId}" // TODO: Get actual station name
            tvTime.text = formatTimeRange(booking.startTime, booking.endTime)
            
            // Set status chip
            chipStatus.text = booking.status
            when (booking.status.lowercase()) {
                "pending" -> chipStatus.setChipBackgroundColorResource(R.color.md_theme_light_tertiaryContainer)
                "approved" -> chipStatus.setChipBackgroundColorResource(R.color.md_theme_light_primaryContainer)
                "completed" -> chipStatus.setChipBackgroundColorResource(R.color.md_theme_light_secondaryContainer)
                else -> chipStatus.setChipBackgroundColorResource(R.color.md_theme_light_surfaceVariant)
            }
            
            // Handle QR button
            btnViewQr.isEnabled = booking.isApproved && !booking.qrPayload.isNullOrEmpty()
            btnViewQr.setOnClickListener {
                if (booking.isApproved && !booking.qrPayload.isNullOrEmpty()) {
                    // TODO: Open QR code activity
                }
            }
            
            // Handle modify button
            val canModify = com.evcharge.mobile.util.DateTimeRules.canModifyOrCancel(
                com.evcharge.mobile.util.Time.parseApiTimestamp(booking.startTime)
            )
            btnModify.isEnabled = canModify
            btnModify.setOnClickListener {
                if (canModify) {
                    // TODO: Open modify booking activity
                }
            }
            
            itemView.setOnClickListener {
                onItemClick(booking)
            }
        }
        
        private fun formatTimeRange(startTime: String, endTime: String): String {
            val start = com.evcharge.mobile.util.Time.parseApiTimestamp(startTime)
            val end = com.evcharge.mobile.util.Time.parseApiTimestamp(endTime)
            return "${DateTimeRules.formatDateTime(start)} - ${DateTimeRules.formatDateTime(end)}"
        }
    }
    
    class BookingDiffCallback : DiffUtil.ItemCallback<BookingResponse>() {
        override fun areItemsTheSame(oldItem: BookingResponse, newItem: BookingResponse): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: BookingResponse, newItem: BookingResponse): Boolean {
            return oldItem == newItem
        }
    }
}
