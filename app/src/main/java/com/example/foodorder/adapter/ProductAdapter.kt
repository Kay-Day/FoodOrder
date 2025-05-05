package com.example.foodorder.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorder.R
import com.example.foodorder.model.Product
import com.example.foodorder.util.ImageUtils
import java.text.NumberFormat
import java.util.*

class ProductAdapter(
    private val context: Context,
    private val products: List<Product>,
    private val listener: OnProductClickListener,
    private val isCustomer: Boolean = true
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface OnProductClickListener {
        fun onProductClick(product: Product)
        fun onAddToCartClick(product: Product)
        fun onEditClick(product: Product)
        fun onDeleteClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutRes = if (isCustomer) R.layout.item_product else R.layout.item_product_seller
        val view = LayoutInflater.from(context).inflate(layoutRes, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        private val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvProductCategory)
        private val btnAddToCart: Button? = if (isCustomer) itemView.findViewById(R.id.btnAddToCart) else null
        private val btnEdit: Button? = if (!isCustomer) itemView.findViewById(R.id.btnEdit) else null
        private val btnDelete: Button? = if (!isCustomer) itemView.findViewById(R.id.btnDelete) else null

        fun bind(product: Product) {
            tvName.text = product.name

            // Format price as currency
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            tvPrice.text = numberFormat.format(product.price)

            // Set category text
            tvCategory.text = if (product.category == "food") "Thức ăn" else "Thức uống"

            // Load image
            if (product.image.isNotEmpty()) {
                try {
                    val bitmap = ImageUtils.base64ToBitmap(product.image)
                    imgProduct.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    // If the image can't be loaded, set a placeholder
                    imgProduct.setImageResource(R.drawable.ic_food_placeholder)
                }
            } else {
                imgProduct.setImageResource(R.drawable.ic_food_placeholder)
            }

            // Set click listeners
            itemView.setOnClickListener {
                listener.onProductClick(product)
            }

            if (isCustomer) {
                btnAddToCart?.setOnClickListener {
                    listener.onAddToCartClick(product)
                }
            } else {
                btnEdit?.setOnClickListener {
                    listener.onEditClick(product)
                }

                btnDelete?.setOnClickListener {
                    listener.onDeleteClick(product)
                }
            }
        }
    }
}