package com.example.foodorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.R
import com.example.foodorder.model.Notification
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val context: Context,
    private val notifications: List<Notification>,
    private val listener: OnNotificationClickListener
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    interface OnNotificationClickListener {
        fun onNotificationClick(notification: Notification, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification, position)
    }

    override fun getItemCount(): Int = notifications.size

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        private val tvDate: TextView = itemView.findViewById(R.id.tvNotificationDate)

        fun bind(notification: Notification, position: Int) {
            tvTitle.text = notification.title
            tvMessage.text = notification.message

            // Format date
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(notification.date)
                tvDate.text = date?.let { outputFormat.format(it) } ?: notification.date
            } catch (e: Exception) {
                tvDate.text = notification.date
            }

            // Set background color based on read status
            if (notification.isRead) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.notification_read))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.notification_unread))
            }

            // Set click listener
            itemView.setOnClickListener {
                listener.onNotificationClick(notification, position)
            }
        }
    }
}