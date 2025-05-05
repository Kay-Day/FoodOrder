package com.example.foodorder.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorder.R
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.ui.customer.CustomerMainActivity
import com.example.foodorder.ui.seller.SellerMainActivity
import com.example.foodorder.util.PreferenceManager

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.loginProgressBar)

        // Initialize helpers
        databaseHelper = DatabaseHelper(this)
        preferenceManager = PreferenceManager(this)

        // Check if user is already logged in
        if (preferenceManager.isLoggedIn()) {
            navigateToMainScreen()
        }

        // Set click listeners
        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate inputs
        if (email.isEmpty()) {
            etEmail.error = "Email không được để trống"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Mật khẩu không được để trống"
            etPassword.requestFocus()
            return
        }

        // Show progress
        progressBar.visibility = View.VISIBLE

        // Check user credentials in database
        val user = databaseHelper.getUserByEmail(email)

        if (user != null && user.password == password) {
            // Save user details
            preferenceManager.setLoggedIn(true)
            preferenceManager.saveUserDetails(user.id, user.name, user.email, user.userType)

            // Navigate to main screen
            navigateToMainScreen()
        } else {
            // Authentication failed
            Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }
    }

    private fun navigateToMainScreen() {
        val intent = if (preferenceManager.getUserType() == "seller") {
            Intent(this, SellerMainActivity::class.java)
        } else {
            Intent(this, CustomerMainActivity::class.java)
        }

        // Clear back stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}