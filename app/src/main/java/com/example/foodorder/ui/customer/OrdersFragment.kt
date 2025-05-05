package com.example.foodorder.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.foodorder.R
import com.example.foodorder.adapter.OrderAdapter
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.Order
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
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        // Initialize views
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders)
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

        val userId = preferenceManager.getUserId()

        // Get orders
        orderList = databaseHelper.getUserOrders(userId)

        // Update UI
        updateUI()

        // Hide refresh indicator
        swipeRefreshLayout.isRefreshing = false
    }

    private fun updateUI() {
        if (orderList.isNotEmpty()) {
            recyclerViewOrders.visibility = View.VISIBLE
            tvNoOrders.visibility = View.GONE

            val adapter = OrderAdapter(requireContext(), orderList, this)
            recyclerViewOrders.adapter = adapter
        } else {
            recyclerViewOrders.visibility = View.GONE
            tvNoOrders.visibility = View.VISIBLE
        }
    }

    override fun onOrderClick(order: Order) {
        // Open order detail activity
        val intent = Intent(requireContext(), OrderDetailActivity::class.java)
        intent.putExtra("order_id", order.id)
        startActivity(intent)
    }

    override fun onConfirmOrder(order: Order, position: Int) {
        // Not used in customer view
    }

    override fun onDeliverOrder(order: Order, position: Int) {
        // Not used in customer view
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }
}