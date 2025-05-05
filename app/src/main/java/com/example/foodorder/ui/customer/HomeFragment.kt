package com.example.foodorder.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.foodorder.R
import com.example.foodorder.adapter.ProductAdapter
import com.example.foodorder.database.DatabaseHelper
import com.example.foodorder.model.Cart
import com.example.foodorder.model.Product
import com.example.foodorder.util.PreferenceManager

class HomeFragment : Fragment(), ProductAdapter.OnProductClickListener {

    private lateinit var recyclerViewFood: RecyclerView
    private lateinit var recyclerViewDrink: RecyclerView
    private lateinit var tvFoodTitle: TextView
    private lateinit var tvDrinkTitle: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager

    private var foodList: List<Product> = ArrayList()
    private var drinkList: List<Product> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        recyclerViewFood = view.findViewById(R.id.recyclerViewFood)
        recyclerViewDrink = view.findViewById(R.id.recyclerViewDrink)
        tvFoodTitle = view.findViewById(R.id.tvFoodTitle)
        tvDrinkTitle = view.findViewById(R.id.tvDrinkTitle)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        // Initialize helpers
        databaseHelper = DatabaseHelper(requireContext())
        preferenceManager = PreferenceManager(requireContext())

        // Setup RecyclerViews
        setupRecyclerViews()

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadProducts()
        }

        // Load products
        loadProducts()

        return view
    }

    private fun setupRecyclerViews() {
        // Food RecyclerView
        recyclerViewFood.layoutManager = GridLayoutManager(requireContext(), 2)

        // Drink RecyclerView
        recyclerViewDrink.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun loadProducts() {
        // Show refresh indicator
        swipeRefreshLayout.isRefreshing = true

        // Get food products
        foodList = databaseHelper.getProductsByCategory("food")

        // Get drink products
        drinkList = databaseHelper.getProductsByCategory("drink")

        // Update UI
        updateUI()

        // Hide refresh indicator
        swipeRefreshLayout.isRefreshing = false
    }

    private fun updateUI() {
        // Update food section
        if (foodList.isNotEmpty()) {
            tvFoodTitle.visibility = View.VISIBLE
            recyclerViewFood.visibility = View.VISIBLE

            val foodAdapter = ProductAdapter(requireContext(), foodList, this)
            recyclerViewFood.adapter = foodAdapter
        } else {
            tvFoodTitle.visibility = View.GONE
            recyclerViewFood.visibility = View.GONE
        }

        // Update drink section
        if (drinkList.isNotEmpty()) {
            tvDrinkTitle.visibility = View.VISIBLE
            recyclerViewDrink.visibility = View.VISIBLE

            val drinkAdapter = ProductAdapter(requireContext(), drinkList, this)
            recyclerViewDrink.adapter = drinkAdapter
        } else {
            tvDrinkTitle.visibility = View.GONE
            recyclerViewDrink.visibility = View.GONE
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

    override fun onResume() {
        super.onResume()
        loadProducts()
    }
}