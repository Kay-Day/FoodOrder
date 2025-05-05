package com.example.foodorder.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.R
import com.example.foodorder.adapter.CartAdapter
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.CartItem
import com.example.foodorder.model.Order
import com.example.foodorder.model.OrderDetail
import com.example.foodorder.util.NotificationUtils
import com.example.foodorder.util.PreferenceManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class CartFragment : Fragment(), CartAdapter.OnCartItemActionListener {

    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var tvEmptyCart: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnCheckout: Button

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    private var cartItems: List<CartItem> = ArrayList()
    private var totalAmount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        // Initialize views
        recyclerViewCart = view.findViewById(R.id.recyclerViewCart)
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart)
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
        btnCheckout = view.findViewById(R.id.btnCheckout)

        // Initialize helpers
        databaseHelper = DatabaseHelper(requireContext())
        preferenceManager = PreferenceManager(requireContext())

        // Setup RecyclerView
        recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())

        // Set click listener for checkout button
        btnCheckout.setOnClickListener {
            showCheckoutConfirmation()
        }

        // Load cart items
        loadCartItems()

        return view
    }

    private fun loadCartItems() {
        val userId = preferenceManager.getUserId()

        // Get cart items
        cartItems = databaseHelper.getCartItems(userId)

        // Calculate total amount
        totalAmount = cartItems.sumByDouble { it.getSubtotal() }

        // Update UI
        updateUI()
    }

    private fun updateUI() {
        if (cartItems.isNotEmpty()) {
            recyclerViewCart.visibility = View.VISIBLE
            tvEmptyCart.visibility = View.GONE
            btnCheckout.visibility = View.VISIBLE

            val adapter = CartAdapter(requireContext(), cartItems, this)
            recyclerViewCart.adapter = adapter

            // Format and display total amount
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            tvTotalAmount.text = "Tổng tiền: ${numberFormat.format(totalAmount)}"
        } else {
            recyclerViewCart.visibility = View.GONE
            tvEmptyCart.visibility = View.VISIBLE
            btnCheckout.visibility = View.GONE
            tvTotalAmount.text = "Tổng tiền: 0đ"
        }
    }

    override fun onIncrementQuantity(cartItem: CartItem, position: Int) {
        // Increment quantity
        val newQuantity = cartItem.quantity + 1

        // Update in database
        databaseHelper.updateCartItemQuantity(cartItem.id, newQuantity)

        // Reload cart items
        loadCartItems()

        // Update cart badge
        (activity as? CustomerMainActivity)?.updateCartBadge()
    }

    override fun onDecrementQuantity(cartItem: CartItem, position: Int) {
        // Ensure quantity doesn't go below 1
        if (cartItem.quantity <= 1) {
            // Show remove confirmation
            showRemoveItemConfirmation(cartItem, position)
            return
        }

        // Decrement quantity
        val newQuantity = cartItem.quantity - 1

        // Update in database
        databaseHelper.updateCartItemQuantity(cartItem.id, newQuantity)

        // Reload cart items
        loadCartItems()

        // Update cart badge
        (activity as? CustomerMainActivity)?.updateCartBadge()
    }

    override fun onRemoveItem(cartItem: CartItem, position: Int) {
        showRemoveItemConfirmation(cartItem, position)
    }

    private fun showRemoveItemConfirmation(cartItem: CartItem, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa món ăn")
            .setMessage("Bạn có chắc chắn muốn xóa ${cartItem.productName} khỏi giỏ hàng?")
            .setPositiveButton("Xóa") { _, _ ->
                // Remove from database
                databaseHelper.removeFromCart(cartItem.id)

                // Reload cart items
                loadCartItems()

                // Update cart badge
                (activity as? CustomerMainActivity)?.updateCartBadge()

                // Show success message
                Toast.makeText(requireContext(), "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showCheckoutConfirmation() {
        val user = databaseHelper.getUserById(preferenceManager.getUserId())

        if (user == null) {
            Toast.makeText(requireContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            return
        }

        // Format total amount
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        val formattedTotal = numberFormat.format(totalAmount)

        // Create confirmation message
        val message = """
            Thông tin giao hàng:
            Tên: ${user.name}
            Địa chỉ: ${user.address}
            Số điện thoại: ${user.phone}
            
            Tổng tiền: $formattedTotal
            
            Hình thức thanh toán: Thanh toán tại nhà
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận đặt hàng")
            .setMessage(message)
            .setPositiveButton("Đặt hàng") { _, _ ->
                processOrder()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun processOrder() {
        val userId = preferenceManager.getUserId()

        // Create date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        // Create order
        val order = Order(
            userId = userId,
            date = currentDate,
            total = totalAmount,
            status = "placed"
        )

        // Create order details
        val orderDetails = cartItems.map { cartItem ->
            OrderDetail(
                productId = cartItem.productId,
                quantity = cartItem.quantity,
                price = cartItem.productPrice
            )
        }

        // Save order to database
        val orderId = databaseHelper.createOrder(order, orderDetails).toInt()

        if (orderId > 0) {
            // Clear cart
            databaseHelper.clearCart(userId)

            // Show success message
            Toast.makeText(requireContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show()

            // Reload cart items
            loadCartItems()

            // Update cart badge
            (activity as? CustomerMainActivity)?.updateCartBadge()

            // Send notification to customer
            NotificationUtils.saveNotification(
                requireContext(),
                userId,
                "Đặt hàng thành công",
                "Đơn hàng #$orderId của bạn đã được đặt thành công và đang chờ xác nhận."
            )

            // Notify all sellers
            val sellerIds = HashSet<Int>()

            // Get unique seller IDs from cart items
            for (cartItem in cartItems) {
                val product = databaseHelper.getProductById(cartItem.productId)
                product?.let {
                    sellerIds.add(it.sellerId)
                }
            }

            // Send notification to each seller
            for (sellerId in sellerIds) {
                NotificationUtils.sendNewOrderNotification(requireContext(), sellerId, orderId)
            }

            // Navigate to orders fragment
            (activity as? CustomerMainActivity)?.bottomNavigationView?.selectedItemId = R.id.nav_orders
        } else {
            // Show error message
            Toast.makeText(requireContext(), "Đặt hàng không thành công", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCartItems()
    }
}