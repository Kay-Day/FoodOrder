package com.example.foodorder.ui.seller

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorder.R
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.Product
import com.example.foodorder.util.ImageUtils
import com.example.foodorder.util.PreferenceManager

class AddEditProductActivity : AppCompatActivity() {

    private lateinit var imgProduct: ImageView
    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var rbFood: RadioButton
    private lateinit var rbDrink: RadioButton
    private lateinit var btnChooseImage: Button
    private lateinit var btnSave: Button

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    private var productId: Int = 0
    private var product: Product? = null
    private var selectedImageUri: Uri? = null
    private var isEditMode: Boolean = false

    companion object {
        private const val REQUEST_GALLERY = 1001
        private const val REQUEST_CAMERA = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_product)

        // Initialize views
        imgProduct = findViewById(R.id.imgAddEditProduct)
        etName = findViewById(R.id.etProductName)
        etDescription = findViewById(R.id.etProductDescription)
        etPrice = findViewById(R.id.etProductPrice)
        radioGroup = findViewById(R.id.radioGroupCategory)
        rbFood = findViewById(R.id.rbFood)
        rbDrink = findViewById(R.id.rbDrink)
        btnChooseImage = findViewById(R.id.btnChooseProductImage)
        btnSave = findViewById(R.id.btnSaveProduct)

        // Initialize helpers
        databaseHelper = DatabaseHelper(this)
        preferenceManager = PreferenceManager(this)

        // Get product ID from intent
        productId = intent.getIntExtra("product_id", 0)
        isEditMode = (productId > 0)

        // Setup action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (isEditMode) "Chỉnh sửa sản phẩm" else "Thêm sản phẩm mới"

        // Set default selection
        rbFood.isChecked = true

        // Set click listeners
        btnChooseImage.setOnClickListener {
            showImageSelectionDialog()
        }

        btnSave.setOnClickListener {
            saveProduct()
        }

        // Load product data if in edit mode
        if (isEditMode) {
            loadProductData()
        }
    }

    private fun loadProductData() {
        product = databaseHelper.getProductById(productId)

        product?.let {
            // Set product data
            etName.setText(it.name)
            etDescription.setText(it.description)
            etPrice.setText(it.price.toString())

            // Set category
            if (it.category == "food") {
                rbFood.isChecked = true
            } else {
                rbDrink.isChecked = true
            }

            // Load image
            if (it.image.isNotEmpty()) {
                try {
                    val bitmap = ImageUtils.base64ToBitmap(it.image)
                    imgProduct.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    imgProduct.setImageResource(R.drawable.ic_food_placeholder)
                }
            }
        }
    }

    private fun showImageSelectionDialog() {
        val options = arrayOf("Thư viện ảnh", "Chụp ảnh")

        AlertDialog.Builder(this)
            .setTitle("Chọn ảnh sản phẩm")
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
                            val inputStream = contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            imgProduct.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            Toast.makeText(this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                REQUEST_CAMERA -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        imgProduct.setImageBitmap(it)

                        // Convert bitmap to URI
                        val path = MediaStore.Images.Media.insertImage(
                            contentResolver,
                            bitmap, "Product Image", null
                        )
                        selectedImageUri = Uri.parse(path)
                    }
                }
            }
        }
    }

    private fun saveProduct() {
        val name = etName.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val priceStr = etPrice.text.toString().trim()
        val category = if (rbFood.isChecked) "food" else "drink"

        // Validate inputs
        if (name.isEmpty()) {
            etName.error = "Tên sản phẩm không được để trống"
            etName.requestFocus()
            return
        }

        if (description.isEmpty()) {
            etDescription.error = "Mô tả không được để trống"
            etDescription.requestFocus()
            return
        }

        if (priceStr.isEmpty()) {
            etPrice.error = "Giá không được để trống"
            etPrice.requestFocus()
            return
        }

        val price = try {
            priceStr.toDouble()
        } catch (e: Exception) {
            etPrice.error = "Giá không hợp lệ"
            etPrice.requestFocus()
            return
        }

        if (price <= 0) {
            etPrice.error = "Giá phải lớn hơn 0"
            etPrice.requestFocus()
            return
        }

        // Get base64 image string
        var base64Image = ""

        if (selectedImageUri != null) {
            try {
                val imagePath = ImageUtils.saveImageToStorage(this, selectedImageUri!!)

                // Convert image to base64 string
                val bitmap = BitmapFactory.decodeFile(imagePath)
                val resizedBitmap = ImageUtils.resizeBitmap(bitmap, 500, 500)
                base64Image = ImageUtils.bitmapToBase64(resizedBitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Không thể lưu ảnh", Toast.LENGTH_SHORT).show()
            }
        } else if (isEditMode && product?.image?.isNotEmpty() == true) {
            // Keep existing image if no new image selected
            base64Image = product?.image ?: ""
        }

        // Create or update product
        val sellerId = preferenceManager.getUserId()

        if (isEditMode) {
            // Update existing product
            product?.let {
                it.name = name
                it.description = description
                it.price = price
                it.category = category

                if (base64Image.isNotEmpty()) {
                    it.image = base64Image
                }

                val result = databaseHelper.updateProduct(it)

                if (result > 0) {
                    Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Cập nhật sản phẩm không thành công", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Create new product
            val newProduct = Product(
                name = name,
                description = description,
                price = price,
                image = base64Image,
                category = category,
                sellerId = sellerId
            )

            val result = databaseHelper.addProduct(newProduct)

            if (result > 0) {
                Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Thêm sản phẩm không thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}