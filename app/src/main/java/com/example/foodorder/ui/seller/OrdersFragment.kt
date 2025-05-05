package com.example.foodorder.ui.seller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.foodorder.R
import com.example.foodorder.adapter.OrderAdapter
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.Order
import com.example.foodorder.util.NotificationUtils
import com.example.foodorder.util.PreferenceManager

class OrdersFragment : Fragment(), OrderAdapter.OnOrderClickListener {

    private lateinit var recyclerViewOrders: RecyclerView
    private lateinit var tvNoOrders: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    private var orderList: List<Order> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_seller_orders, container, false)

        // Initialize views
        recyclerViewOrders = view.findViewById(R.id.recyclerViewSellerOrders)
        tvNoOrders = view.findViewById(R.id.tvNoOrders)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        // Initialize helpers
        databaseHelper = DatabaseHelper(requireContext())
        preferenceManager = PreferenceManager(requireContext())

        // Setup RecyclerView
        recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadOrders()
        }

        // Load orders
        loadOrders()

        return view
    }

    private fun loadOrders() {
        // Show refresh indicator
        swipeRefreshLayout.isRefreshing = true

        val sellerId = preferenceManager.getUserId()

        // Get orders for seller's products
        orderList = databaseHelper.getSellerOrders(sellerId)

        // Update UI
        updateUI()

        // Hide refresh indicator
        swipeRefreshLayout.isRefreshing = false

        // Update order badge
        (activity as? SellerMainActivity)?.updateOrderBadge()
    }

    private fun updateUI() {
        if (orderList.isNotEmpty()) {
            recyclerViewOrders.visibility = View.VISIBLE
            tvNoOrders.visibility = View.GONE

            val adapter = OrderAdapter(requireContext(), orderList, this, true)
            recyclerViewOrders.adapter = adapter
        } else {
            recyclerViewOrders.visibility = View.GONE
            tvNoOrders.visibility = View.VISIBLE
        }
    }

    override fun onOrderClick(order: Order) {
        // Open order detail activity
        val intent = Intent(requireContext(), SellerOrderDetailActivity::class.java)
        intent.putExtra("order_id", order.id)
        startActivity(intent)
    }

    override fun onConfirmOrder(order: Order, position: Int) {
        // Update order status to confirmed
        order.status = "confirmed"
        val result = databaseHelper.updateOrderStatus(order.id, "confirmed")

        if (result > 0) {
            // Show success message
            Toast.makeText(requireContext(), "Đơn hàng đã được xác nhận", Toast.LENGTH_SHORT).show()

            // Refresh order list
            loadOrders()

            // Send notification to customer
            NotificationUtils.sendOrderStatusNotification(
                requireContext(),
                order.userId,
                order.id,
                "confirmed"
            )
        } else {
            Toast.makeText(requireContext(), "Không thể xác nhận đơn hàng", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeliverOrder(order: Order, position: Int) {
        // Update order status to delivered
        order.status = "delivered"
        val result = databaseHelper.updateOrderStatus(order.id, "delivered")

        if (result > 0) {
            // Show success message
            Toast.makeText(requireContext(), "Đơn hàng đã được giao", Toast.LENGTH_SHORT).show()

            // Refresh order list
            loadOrders()

            // Send notification to customer
            NotificationUtils.sendOrderStatusNotification(
                requireContext(),
                order.userId,
                order.id,
                "delivered"
            )
        } else {
            Toast.makeText(requireContext(), "Không thể cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }
}