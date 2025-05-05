package com.example.foodorder.ui.customer

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

class CustomerMainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager
    private var cartBadge: BadgeDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main)

        // Initialize helpers
        databaseHelper = DatabaseHelper(this)
        preferenceManager = PreferenceManager(this)

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.customerBottomNav)
        setupBottomNavigation()

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Update cart badge
        updateCartBadge()
    }

    private fun setupBottomNavigation() {
        // Create cart badge
        cartBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart)
        cartBadge?.isVisible = false

        // Set navigation listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.nav_orders -> {
                    loadFragment(OrdersFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.customerFragmentContainer, fragment)
            .commit()
    }

    fun updateCartBadge() {
        val userId = preferenceManager.getUserId()
        val cartItems = databaseHelper.getCartItems(userId)

        if (cartItems.isNotEmpty()) {
            cartBadge?.isVisible = true
            cartBadge?.number = cartItems.size
        } else {
            cartBadge?.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }
}