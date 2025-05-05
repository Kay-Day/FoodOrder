package com.example.foodorder.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorder.R
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.User
import com.example.foodorder.ui.customer.CustomerMainActivity
import com.example.foodorder.ui.seller.SellerMainActivity
import com.example.foodorder.util.PreferenceManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var rbCustomer: RadioButton
    private lateinit var rbSeller: RadioButton
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etName = findViewById(R.id.etRegisterName)
        etEmail = findViewById(R.id.etRegisterEmail)
        etPassword = findViewById(R.id.etRegisterPassword)
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        etPhone = findViewById(R.id.etRegisterPhone)
        etAddress = findViewById(R.id.etRegisterAddress)
        radioGroup = findViewById(R.id.radioGroupUserType)
        rbCustomer = findViewById(R.id.rbCustomer)
        rbSeller = findViewById(R.id.rbSeller)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        progressBar = findViewById(R.id.registerProgressBar)

        // Initialize helpers
        databaseHelper = DatabaseHelper(this)
        preferenceManager = PreferenceManager(this)

        // Set default selection
        rbCustomer.isChecked = true

        // Set click listeners
        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLogin.setOnClickListener {
            finish() // Go back to login screen
        }
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val userType = if (rbSeller.isChecked) "seller" else "customer"

        // Validate inputs
        if (name.isEmpty()) {
            etName.error = "Tên không được để trống"
            etName.requestFocus()
            return
        }

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

        if (password.length < 6) {
            etPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
            etPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty() || confirmPassword != password) {
            etConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            etConfirmPassword.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            etPhone.error = "Số điện thoại không được để trống"
            etPhone.requestFocus()
            return
        }

        if (address.isEmpty()) {
            etAddress.error = "Địa chỉ không được để trống"
            etAddress.requestFocus()
            return
        }

        // Show progress
        progressBar.visibility = View.VISIBLE

        // Check if email already exists
        val existingUser = databaseHelper.getUserByEmail(email)
        if (existingUser != null) {
            etEmail.error = "Email đã được sử dụng"
            etEmail.requestFocus()
            progressBar.visibility = View.GONE
            return
        }

        // Create new user
        val user = User(
            name = name,
            email = email,
            password = password,
            phone = phone,
            address = address,
            userType = userType
        )

        // Insert user to database
        val userId = databaseHelper.addUser(user).toInt()

        if (userId > 0) {
            // Save user details
            preferenceManager.setLoggedIn(true)
            preferenceManager.saveUserDetails(userId, name, email, userType)

            // Navigate to main screen
            val intent = if (userType == "seller") {
                Intent(this, SellerMainActivity::class.java)
            } else {
                Intent(this, CustomerMainActivity::class.java)
            }

            // Clear back stack
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        } else {
            // Registration failed
            Toast.makeText(this, "Đăng ký không thành công. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }
    }
}