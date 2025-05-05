package com.example.foodorder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorder.ui.auth.LoginActivity
import com.example.foodorder.ui.customer.CustomerMainActivity
import com.example.foodorder.ui.seller.SellerMainActivity
import com.example.foodorder.util.NotificationUtils
import com.example.foodorder.util.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize preference manager
        preferenceManager = PreferenceManager(this)

        // Create notification channel
        NotificationUtils.createNotificationChannel(this)

        // Delay for splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserLoginStatus()
        }, 2000) // 2 seconds delay
    }

    private fun checkUserLoginStatus() {
        val intent = if (preferenceManager.isLoggedIn()) {
            // User is logged in, determine which main activity to open
            if (preferenceManager.getUserType() == "seller") {
                Intent(this, SellerMainActivity::class.java)
            } else {
                Intent(this, CustomerMainActivity::class.java)
            }
        } else {
            // User is not logged in, go to login screen
            Intent(this, LoginActivity::class.java)
        }

        // Start the appropriate activity
        startActivity(intent)
        finish() // Close this activity
    }
}