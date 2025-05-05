package com.example.foodorder.ui.customer

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorder.R
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.Cart
import com.example.foodorder.model.Product
import com.example.foodorder.util.ImageUtils
import com.example.foodorder.util.PreferenceManager
import java.text.NumberFormat
import java.util.*

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var imgProduct: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var btnDecrement: Button
    private lateinit var btnIncrement: Button
    private lateinit var btnAddToCart: Button

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    private var productId: Int = 0
    private var product: Product? = null
    private var quantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Initialize views
        imgProduct = findViewById(R.id.imgProductDetail)
        tvName = findViewById(R.id.tvProductDetailName)
        tvDescription = findViewById(R.id.tvProductDetailDescription)
        tvPrice = findViewById(R.id.tvProductDetailPrice)
        tvCategory = findViewById(R.id.tvProductDetailCategory)
        tvQuantity = findViewById(R.id.tvQuantity)
        btnDecrement = findViewById(R.id.btnDecrementQuantity)
        btnIncrement = findViewById(R.id.btnIncrementQuantity)
        btnAddToCart = findViewById(R.id.btnAddToCartDetail)

        // Initialize helpers
        databaseHelper = DatabaseHelper(this)
        preferenceManager = PreferenceManager(this)

        // Get product ID from intent
        productId = intent.getIntExtra("product_id", 0)

        // Setup action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chi tiết sản phẩm"

        // Set initial quantity
        tvQuantity.text = quantity.toString()

        // Set click listeners
        btnDecrement.setOnClickListener {
            decrementQuantity()
        }

        btnIncrement.setOnClickListener {
            incrementQuantity()
        }

        btnAddToCart.setOnClickListener {
            addToCart()
        }

        // Load product details
        loadProductDetails()
    }

    private fun loadProductDetails() {
        if (productId <= 0) {
            Toast.makeText(this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        product = databaseHelper.getProductById(productId)

        product?.let {
            // Set product data
            tvName.text = it.name
            tvDescription.text = it.description

            // Format price
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            tvPrice.text = numberFormat.format(it.price)

            // Set category
            tvCategory.text = if (it.category == "food") "Thức ăn" else "Thức uống"

            // Load image
            if (it.image.isNotEmpty()) {
                try {
                    val bitmap = ImageUtils.base64ToBitmap(it.image)
                    imgProduct.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    imgProduct.setImageResource(R.drawable.ic_food_placeholder)
                }
            } else {
                imgProduct.setImageResource(R.drawable.ic_food_placeholder)
            }
        } ?: run {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun decrementQuantity() {
        if (quantity > 1) {
            quantity--
            tvQuantity.text = quantity.toString()
        }
    }

    private fun incrementQuantity() {
        quantity++
        tvQuantity.text = quantity.toString()
    }

    private fun addToCart() {
        val userId = preferenceManager.getUserId()

        product?.let {
            // Create cart item
            val cart = Cart(
                userId = userId,
                productId = it.id,
                quantity = quantity
            )

            // Add to cart
            val result = databaseHelper.addToCart(cart)

            if (result > 0) {
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
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