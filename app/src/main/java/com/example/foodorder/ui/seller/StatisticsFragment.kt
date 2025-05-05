package com.example.foodorder.ui.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.foodorder.R
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.util.PreferenceManager
import java.text.NumberFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private lateinit var tvTotalProducts: TextView
    private lateinit var tvTotalOrders: TextView
    private lateinit var tvTotalRevenue: TextView
    private lateinit var llTopProducts: LinearLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        // Initialize views
        tvTotalProducts = view.findViewById(R.id.tvTotalProducts)
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders)
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue)
        llTopProducts = view.findViewById(R.id.llTopProducts)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        // Initialize helpers
        databaseHelper = DatabaseHelper(requireContext())
        preferenceManager = PreferenceManager(requireContext())

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadStatistics()
        }

        // Load statistics
        loadStatistics()

        return view
    }

    private fun loadStatistics() {
        // Show refresh indicator
        swipeRefreshLayout.isRefreshing = true

        val sellerId = preferenceManager.getUserId()

        // Get statistics
        val stats = databaseHelper.getSellerStatistics(sellerId)

        // Update UI
        updateUI(stats)

        // Hide refresh indicator
        swipeRefreshLayout.isRefreshing = false
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateUI(stats: Map<String, Any>) {
        // Set total products
        val totalProducts = stats["totalProducts"] as Int
        tvTotalProducts.text = totalProducts.toString()

        // Set total orders
        val totalOrders = stats["totalOrders"] as Int
        tvTotalOrders.text = totalOrders.toString()

        // Set total revenue
        val totalRevenue = stats["totalRevenue"] as Double
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        tvTotalRevenue.text = numberFormat.format(totalRevenue)

        // Clear previous top products
        llTopProducts.removeAllViews()

        // Display top products
        val topProducts = stats["topProducts"] as List<Map<String, Any>>

        if (topProducts.isNotEmpty()) {
            for (product in topProducts) {
                val itemView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_top_product, llTopProducts, false)

                val tvProductName = itemView.findViewById<TextView>(R.id.tvTopProductName)
                val tvProductSold = itemView.findViewById<TextView>(R.id.tvTopProductSold)

                tvProductName.text = product["name"] as String
                tvProductSold.text = "Đã bán: ${product["totalSold"]}"

                llTopProducts.addView(itemView)
            }
        } else {
            // No top products to display
            val noDataView = TextView(requireContext())
            noDataView.text = "Chưa có dữ liệu bán hàng"
            noDataView.setPadding(16, 16, 16, 16)
            llTopProducts.addView(noDataView)
        }
    }

    override fun onResume() {
        super.onResume()
        loadStatistics()
    }
}