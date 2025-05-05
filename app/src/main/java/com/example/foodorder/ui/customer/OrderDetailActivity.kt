package com.example.foodorder.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.foodorder.R
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.Order
import com.example.foodorder.model.User
import com.example.foodorder.util.PreferenceManager
import de.hdodenhof.circleimageview.CircleImageView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var tvOrderId: TextView
    private lateinit var tvOrderDate: TextView
    private lateinit var tvOrderStatus: TextView
    private lateinit var tvOrderTotal: TextView
    private lateinit var llOrderItems: LinearLayout
    private lateinit var tvCustomerName: TextView
    private lateinit var tvCustomerPhone: TextView
    private lateinit var tvCustomerAddress: TextView

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    private var orderId: Int = 0
    private var order: Order? = null
    private var customer: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        // Initialize views
        tvOrderId = findViewById(R.id.tvOrderDetailId)
        tvOrderDate = findViewById(R.id.tvOrderDetailDate)
        tvOrderStatus = findViewById(R.id.tvOrderDetailStatus)
        tvOrderTotal = findViewById(R.id.tvOrderDetailTotal)
        llOrderItems = findViewById(R.id.llOrderItems)
        tvCustomerName = findViewById(R.id.tvCustomerName)
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone)
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress)

        // Initialize helpers
        databaseHelper = DatabaseHelper(this)
        preferenceManager = PreferenceManager(this)

        // Get order ID from intent
        orderId = intent.getIntExtra("order_id", 0)

        // Setup action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chi tiết đơn hàng"

        // Load order details
        loadOrderDetails()
    }

    private fun loadOrderDetails() {
        if (orderId <= 0) {
            finish()
            return
        }

        // Get order
        order = databaseHelper.getOrderById(orderId)

        order?.let {
            // Get customer
            customer = databaseHelper.getUserById(it.userId)

            // Set order details
            tvOrderId.text = "Đơn hàng #${it.id}"

            // Format date
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(it.date)
                tvOrderDate.text = date?.let { d -> outputFormat.format(d) } ?: it.date
            } catch (e: Exception) {
                tvOrderDate.text = it.date
            }

            // Set status text and color
            when (it.status) {
                "placed" -> {
                    tvOrderStatus.text = "Đã đặt hàng"
                    tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.order_placed))
                }
                "confirmed" -> {
                    tvOrderStatus.text = "Đã xác nhận"
                    tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.order_confirmed))
                }
                "delivered" -> {
                    tvOrderStatus.text = "Đã giao hàng"
                    tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.order_delivered))
                }
                else -> {
                    tvOrderStatus.text = it.status
                    tvOrderStatus.setTextColor(ContextCompat.getColor(this, R.color.black))
                }
            }

            // Format total
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            tvOrderTotal.text = numberFormat.format(it.total)

            // Set customer info
            customer?.let { user ->
                tvCustomerName.text = user.name
                tvCustomerPhone.text = user.phone
                tvCustomerAddress.text = user.address
            }

            // Load order items
            loadOrderItems()
        } ?: run {
            finish()
        }
    }

    private fun loadOrderItems() {
        // Clear previous items
        llOrderItems.removeAllViews()

        // Get order items with product info
        val orderItems = databaseHelper.getOrderDetailWithProductInfo(orderId)

        for (item in orderItems) {
            val orderDetail = item["orderDetail"] as com.example.foodorder.model.OrderDetail
            val productName = item["productName"] as String
            val productImage = item["productImage"] as String

            // Inflate item view
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_order_detail, llOrderItems, false)

            // Get views
            val imgProduct = itemView.findViewById<CircleImageView>(R.id.imgOrderItemProduct)
            val tvName = itemView.findViewById<TextView>(R.id.tvOrderItemName)
            val tvPrice = itemView.findViewById<TextView>(R.id.tvOrderItemPrice)
            val tvQuantity = itemView.findViewById<TextView>(R.id.tvOrderItemQuantity)
            val tvSubtotal = itemView.findViewById<TextView>(R.id.tvOrderItemSubtotal)

            // Set data
            tvName.text = productName

            // Load image
            if (productImage.isNotEmpty()) {
                try {
                    val bitmap = com.example.foodorder.util.ImageUtils.base64ToBitmap(productImage)
                    imgProduct.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    imgProduct.setImageResource(R.drawable.ic_food_placeholder)
                }
            } else {
                imgProduct.setImageResource(R.drawable.ic_food_placeholder)
            }

            // Format price
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            tvPrice.text = numberFormat.format(orderDetail.price)

            // Set quantity
            tvQuantity.text = "x${orderDetail.quantity}"

            // Calculate and set subtotal
            val subtotal = orderDetail.price * orderDetail.quantity
            tvSubtotal.text = numberFormat.format(subtotal)

            // Add to container
            llOrderItems.addView(itemView)
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