package com.example.foodorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.R
import com.example.foodorder.model.CartItem
import com.example.foodorder.util.ImageUtils
import java.text.NumberFormat
import java.util.*

class CartAdapter(
    private val context: Context,
    private val cartItems: List<CartItem>,
    private val listener: OnCartItemActionListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface OnCartItemActionListener {
        fun onIncrementQuantity(cartItem: CartItem, position: Int)
        fun onDecrementQuantity(cartItem: CartItem, position: Int)
        fun onRemoveItem(cartItem: CartItem, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem, position)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProduct: ImageView = itemView.findViewById(R.id.imgCartProduct)
        private val tvName: TextView = itemView.findViewById(R.id.tvCartProductName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvCartProductPrice)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvCartQuantity)
        private val btnIncrement: ImageButton = itemView.findViewById(R.id.btnIncrementQuantity)
        private val btnDecrement: ImageButton = itemView.findViewById(R.id.btnDecrementQuantity)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemoveCartItem)
        private val tvSubtotal: TextView = itemView.findViewById(R.id.tvCartItemSubtotal)

        fun bind(cartItem: CartItem, position: Int) {
            tvName.text = cartItem.productName

            // Format price
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            tvPrice.text = numberFormat.format(cartItem.productPrice)

            // Set quantity
            tvQuantity.text = cartItem.quantity.toString()

            // Set subtotal
            tvSubtotal.text = numberFormat.format(cartItem.getSubtotal())

            // Load image
            if (cartItem.productImage.isNotEmpty()) {
                try {
                    val bitmap = ImageUtils.base64ToBitmap(cartItem.productImage)
                    imgProduct.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    imgProduct.setImageResource(R.drawable.ic_food_placeholder)
                }
            } else {
                imgProduct.setImageResource(R.drawable.ic_food_placeholder)
            }

            // Set click listeners
            btnIncrement.setOnClickListener {
                listener.onIncrementQuantity(cartItem, position)
            }

            btnDecrement.setOnClickListener {
                listener.onDecrementQuantity(cartItem, position)
            }

            btnRemove.setOnClickListener {
                listener.onRemoveItem(cartItem, position)
            }
        }
    }
}