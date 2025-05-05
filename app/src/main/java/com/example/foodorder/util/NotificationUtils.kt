package com.example.foodorder.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.foodorder.R
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.Notification
import java.text.SimpleDateFormat
import java.util.*

object NotificationUtils {

    private const val CHANNEL_ID = "food_order_channel"
    private const val CHANNEL_NAME = "Food Order"
    private const val CHANNEL_DESCRIPTION = "Thông báo từ ứng dụng đặt thức ăn"

    // Create notification channel for Android 8.0 and above
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Show a notification
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        intent: Intent? = null
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create pending intent for notification tap action
        val pendingIntent = if (intent != null) {
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            null
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent)
        }

        // Show the notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    // Save notification to database
    fun saveNotification(context: Context, userId: Int, title: String, message: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val notification = Notification(
            userId = userId,
            title = title,
            message = message,
            date = currentDate,
            isRead = false
        )

        val databaseHelper = DatabaseHelper(context)
        databaseHelper.addNotification(notification)
    }

    // Send order status notification
    fun sendOrderStatusNotification(context: Context, userId: Int, orderId: Int, status: String) {
        val title = when (status) {
            "confirmed" -> "Đơn hàng đã được xác nhận"
            "delivered" -> "Đơn hàng đã được giao"
            else -> "Cập nhật trạng thái đơn hàng"
        }

        val message = when (status) {
            "confirmed" -> "Đơn hàng #$orderId của bạn đã được xác nhận và đang được chuẩn bị."
            "delivered" -> "Đơn hàng #$orderId của bạn đã được giao. Chúc bạn ngon miệng!"
            else -> "Trạng thái đơn hàng #$orderId của bạn đã được cập nhật thành $status."
        }

        // Save to database
        saveNotification(context, userId, title, message)

        // Show notification
        showNotification(context, title, message)
    }

    // Send new order notification to seller
    fun sendNewOrderNotification(context: Context, sellerId: Int, orderId: Int) {
        val title = "Đơn hàng mới"
        val message = "Bạn có đơn hàng mới #$orderId cần được xử lý."

        // Save to database
        saveNotification(context, sellerId, title, message)

        // Show notification
        showNotification(context, title, message)
    }
}