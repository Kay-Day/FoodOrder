package com.example.foodorder.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.R
import com.example.foodorder.adapter.ProductAdapter
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.Cart
import com.example.foodorder.model.Product
import com.example.foodorder.util.PreferenceManager

class SearchFragment : Fragment(), ProductAdapter.OnProductClickListener {

    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageButton
    private lateinit var recyclerViewResults: RecyclerView
    private lateinit var tvNoResults: TextView

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    private var productList: List<Product> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize views
        etSearch = view.findViewById(R.id.etSearch)
        btnSearch = view.findViewById(R.id.btnSearch)
        recyclerViewResults = view.findViewById(R.id.recyclerViewSearchResults)
        tvNoResults = view.findViewById(R.id.tvNoResults)

        // Initialize helpers
        databaseHelper = DatabaseHelper(requireContext())
        preferenceManager = PreferenceManager(requireContext())

        // Setup RecyclerView
        recyclerViewResults.layoutManager = GridLayoutManager(requireContext(), 2)

        // Set click listener for search button
        btnSearch.setOnClickListener {
            searchProducts()
        }

        return view
    }

    private fun searchProducts() {
        val query = etSearch.text.toString().trim()

        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show()
            return
        }

        // Search products
        productList = databaseHelper.searchProducts(query)

        // Update UI
        updateUI()
    }

    private fun updateUI() {
        if (productList.isNotEmpty()) {
            recyclerViewResults.visibility = View.VISIBLE
            tvNoResults.visibility = View.GONE

            val adapter = ProductAdapter(requireContext(), productList, this)
            recyclerViewResults.adapter = adapter
        } else {
            recyclerViewResults.visibility = View.GONE
            tvNoResults.visibility = View.VISIBLE
        }
    }

    override fun onProductClick(product: Product) {
        // Open product detail activity
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("product_id", product.id)
        startActivity(intent)
    }

    override fun onAddToCartClick(product: Product) {
        val userId = preferenceManager.getUserId()

        // Create cart item
        val cart = Cart(
            userId = userId,
            productId = product.id,
            quantity = 1
        )

        // Add to cart
        val result = databaseHelper.addToCart(cart)

        if (result > 0) {
            Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()

            // Update cart badge
            (activity as? CustomerMainActivity)?.updateCartBadge()
        } else {
            Toast.makeText(requireContext(), "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEditClick(product: Product) {
        // Not used in this fragment
    }

    override fun onDeleteClick(product: Product) {
        // Not used in this fragment
    }
}