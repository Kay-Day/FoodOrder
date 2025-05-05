package com.example.foodorder.ui.seller

import android.content.Intent
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.foodorder.R
import com.example.foodorder.adapter.ProductAdapter
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.Product
import com.example.foodorder.util.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductsFragment : Fragment(), ProductAdapter.OnProductClickListener {

    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var tvNoProducts: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fabAddProduct: FloatingActionButton

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    private var productList: List<Product> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        // Initialize views
        recyclerViewProducts = view.findViewById(R.id.recyclerViewSellerProducts)
        tvNoProducts = view.findViewById(R.id.tvNoProducts)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        fabAddProduct = view.findViewById(R.id.fabAddProduct)

        // Initialize helpers
        databaseHelper = DatabaseHelper(requireContext())
        preferenceManager = PreferenceManager(requireContext())

        // Setup RecyclerView
        recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadProducts()
        }

        // Set click listener for FAB
        fabAddProduct.setOnClickListener {
            // Open add product activity
            val intent = Intent(requireContext(), AddEditProductActivity::class.java)
            startActivity(intent)
        }

        // Load products
        loadProducts()

        return view
    }

    private fun loadProducts() {
        // Show refresh indicator
        swipeRefreshLayout.isRefreshing = true

        val sellerId = preferenceManager.getUserId()

        // Get seller's products
        productList = databaseHelper.getProductsBySeller(sellerId)

        // Update UI
        updateUI()

        // Hide refresh indicator
        swipeRefreshLayout.isRefreshing = false
    }

    private fun updateUI() {
        if (productList.isNotEmpty()) {
            recyclerViewProducts.visibility = View.VISIBLE
            tvNoProducts.visibility = View.GONE

            val adapter = ProductAdapter(requireContext(), productList, this, false)
            recyclerViewProducts.adapter = adapter
        } else {
            recyclerViewProducts.visibility = View.GONE
            tvNoProducts.visibility = View.VISIBLE
        }
    }

    override fun onProductClick(product: Product) {
        // Open edit product activity
        val intent = Intent(requireContext(), AddEditProductActivity::class.java)
        intent.putExtra("product_id", product.id)
        startActivity(intent)
    }

    override fun onAddToCartClick(product: Product) {
        // Not used in seller view
    }

    override fun onEditClick(product: Product) {
        // Open edit product activity
        val intent = Intent(requireContext(), AddEditProductActivity::class.java)
        intent.putExtra("product_id", product.id)
        startActivity(intent)
    }

    override fun onDeleteClick(product: Product) {
        showDeleteConfirmation(product)
    }

    private fun showDeleteConfirmation(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa sản phẩm")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '${product.name}'?")
            .setPositiveButton("Xóa") { _, _ ->
                // Delete product
                val result = databaseHelper.deleteProduct(product.id)

                if (result > 0) {
                    Toast.makeText(requireContext(), "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show()
                    loadProducts()
                } else {
                    Toast.makeText(requireContext(), "Không thể xóa sản phẩm", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }
}