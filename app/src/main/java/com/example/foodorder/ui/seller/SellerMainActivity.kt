package com.example.foodorder.ui.seller

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foodorder.R
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.util.PreferenceManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView

class SellerMainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager
    private var orderBadge: BadgeDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_main)

        // Initialize helpers
        databaseHelper = DatabaseHelper(this)
        preferenceManager = PreferenceManager(this)

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.sellerBottomNav)
        setupBottomNavigation()

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(ProductsFragment())
        }

        // Update order badge
        updateOrderBadge()
    }

    private fun setupBottomNavigation() {
        // Create order badge
        orderBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_orders)
        orderBadge?.isVisible = false

        // Set navigation listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_products -> {
                    loadFragment(ProductsFragment())
                    true
                }
                R.id.nav_orders -> {
                    loadFragment(OrdersFragment())
                    true
                }
                R.id.nav_statistics -> {
                    loadFragment(StatisticsFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(SellerProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.sellerFragmentContainer, fragment)
            .commit()
    }

    fun updateOrderBadge() {
        val sellerId = preferenceManager.getUserId()
        val orders = databaseHelper.getSellerOrders(sellerId)

        // Count pending orders (placed but not confirmed)
        val pendingOrders = orders.count { it.status == "placed" }

        if (pendingOrders > 0) {
            orderBadge?.isVisible = true
            orderBadge?.number = pendingOrders
        } else {
            orderBadge?.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        updateOrderBadge()
    }
}