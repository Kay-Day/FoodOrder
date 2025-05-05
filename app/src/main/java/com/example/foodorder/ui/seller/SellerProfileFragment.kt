package com.example.foodorder.ui.seller

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.foodorder.R
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.User
import com.example.foodorder.ui.auth.LoginActivity
import com.example.foodorder.util.ImageUtils
import com.example.foodorder.util.PreferenceManager

class SellerProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnChooseImage: Button
    private lateinit var btnSaveProfile: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnLogout: Button

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    private var currentUser: User? = null
    private var selectedImageUri: Uri? = null

    companion object {
        private const val REQUEST_GALLERY = 1001
        private const val REQUEST_CAMERA = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        imgProfile = view.findViewById(R.id.imgProfile)
        tvName = view.findViewById(R.id.tvProfileName)
        etName = view.findViewById(R.id.etProfileName)
        etEmail = view.findViewById(R.id.etProfileEmail)
        etPhone = view.findViewById(R.id.etProfilePhone)
        etAddress = view.findViewById(R.id.etProfileAddress)
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnChooseImage = view.findViewById(R.id.btnChooseImage)
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile)
        btnChangePassword = view.findViewById(R.id.btnChangePassword)
        btnLogout = view.findViewById(R.id.btnLogout)

        // Initialize helpers
        databaseHelper = DatabaseHelper(requireContext())
        preferenceManager = PreferenceManager(requireContext())

        // Load user profile
        loadUserProfile()

        // Set click listeners
        btnChooseImage.setOnClickListener {
            showImageSelectionDialog()
        }

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }

        btnChangePassword.setOnClickListener {
            changePassword()
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        return view
    }

    private fun loadUserProfile() {
        val userId = preferenceManager.getUserId()
        currentUser = databaseHelper.getUserById(userId)

        currentUser?.let { user ->
            // Set user data
            tvName.text = user.name
            etName.setText(user.name)
            etEmail.setText(user.email)
            etPhone.setText(user.phone)
            etAddress.setText(user.address)

            // Load profile image if exists
            if (user.image.isNotEmpty()) {
                try {
                    val bitmap = ImageUtils.base64ToBitmap(user.image)
                    imgProfile.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    imgProfile.setImageResource(R.drawable.ic_profile_placeholder)
                }
            } else {
                imgProfile.setImageResource(R.drawable.ic_profile_placeholder)
            }
        }
    }

    private fun showImageSelectionDialog() {
        val options = arrayOf("Thư viện ảnh", "Chụp ảnh")

        AlertDialog.Builder(requireContext())
            .setTitle("Chọn ảnh đại diện")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> openCamera()
                }
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        try {
                            val inputStream = requireContext().contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            imgProfile.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Không thể tải ảnh", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                REQUEST_CAMERA -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        imgProfile.setImageBitmap(it)

                        // Convert bitmap to URI
                        val path = MediaStore.Images.Media.insertImage(
                            requireContext().contentResolver,
                            bitmap, "Profile Image", null
                        )
                        selectedImageUri = Uri.parse(path)
                    }
                }
            }
        }
    }

    private fun saveProfile() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()

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

        // Check if email is changed and already exists
        if (email != currentUser?.email) {
            val existingUser = databaseHelper.getUserByEmail(email)
            if (existingUser != null) {
                etEmail.error = "Email đã được sử dụng"
                etEmail.requestFocus()
                return
            }
        }

        // Update user object
        currentUser?.let { user ->
            user.name = name
            user.email = email
            user.phone = phone
            user.address = address

            // Process image if selected
            if (selectedImageUri != null) {
                try {
                    val imagePath = ImageUtils.saveImageToStorage(requireContext(), selectedImageUri!!)

                    // Convert image to base64 string
                    val bitmap = BitmapFactory.decodeFile(imagePath)
                    val resizedBitmap = ImageUtils.resizeBitmap(bitmap, 500, 500)
                    val base64Image = ImageUtils.bitmapToBase64(resizedBitmap)

                    user.image = base64Image
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Không thể lưu ảnh", Toast.LENGTH_SHORT).show()
                }
            }

            // Update in database
            val result = databaseHelper.updateUser(user)

            if (result > 0) {
                // Update preference manager
                preferenceManager.saveUserDetails(user.id, user.name, user.email, user.userType)

                // Show success message
                Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()

                // Update UI
                tvName.text = user.name
            } else {
                Toast.makeText(requireContext(), "Cập nhật thông tin không thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changePassword() {
        val currentPassword = etCurrentPassword.text.toString().trim()
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validate inputs
        if (currentPassword.isEmpty()) {
            etCurrentPassword.error = "Mật khẩu hiện tại không được để trống"
            etCurrentPassword.requestFocus()
            return
        }

        if (newPassword.isEmpty()) {
            etNewPassword.error = "Mật khẩu mới không được để trống"
            etNewPassword.requestFocus()
            return
        }

        if (newPassword.length < 6) {
            etNewPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
            etNewPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty() || confirmPassword != newPassword) {
            etConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            etConfirmPassword.requestFocus()
            return
        }

        // Check current password
        if (currentUser?.password != currentPassword) {
            etCurrentPassword.error = "Mật khẩu hiện tại không đúng"
            etCurrentPassword.requestFocus()
            return
        }

        // Update password
        currentUser?.let { user ->
            user.password = newPassword

            // Update in database
            val result = databaseHelper.updateUser(user)

            if (result > 0) {
                Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()

                // Clear password fields
                etCurrentPassword.text.clear()
                etNewPassword.text.clear()
                etConfirmPassword.text.clear()
            } else {
                Toast.makeText(requireContext(), "Đổi mật khẩu không thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                logout()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun logout() {
        // Clear user session
        preferenceManager.clearUserSession()

        // Navigate to login screen
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}