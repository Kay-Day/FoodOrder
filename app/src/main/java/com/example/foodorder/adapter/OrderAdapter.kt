package com.example.foodorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.R
import com.example.foodorder.model.Order
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val context: Context,
    private val orders: List<Order>,
    private val listener: OnOrderClickListener,
    private val isSeller: Boolean = false
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    interface OnOrderClickListener {
        fun onOrderClick(order: Order)
        fun onConfirmOrder(order: Order, position: Int)
        fun onDeliverOrder(order: Order, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order, position)
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        private val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
        private val btnConfirm: Button? = if (isSeller) itemView.findViewById(R.id.btnConfirmOrder) else null
        private val btnDeliver: Button? = if (isSeller) itemView.findViewById(R.id.btnDeliverOrder) else null

        fun bind(order: Order, position: Int) {
            tvOrderId.text = "Đơn hàng #${order.id}"

            // Format date
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(order.date)
                tvOrderDate.text = date?.let { outputFormat.format(it) } ?: order.date
            } catch (e: Exception) {
                tvOrderDate.text = order.date
            }

            // Format price
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            tvOrderTotal.text = numberFormat.format(order.total)

            // Set status text and color
            when (order.status) {
                "placed" -> {
                    tvOrderStatus.text = "Đã đặt hàng"
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.order_placed))
                }
                "confirmed" -> {
                    tvOrderStatus.text = "Đã xác nhận"
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.order_confirmed))
                }
                "delivered" -> {
                    tvOrderStatus.text = "Đã giao hàng"
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.order_delivered))
                }
                else -> {
                    tvOrderStatus.text = order.status
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            }

            // Set click listener for the whole item
            itemView.setOnClickListener {
                listener.onOrderClick(order)
            }

            // For seller view, show action buttons based on order status
            if (isSeller) {
                when (order.status) {
                    "placed" -> {
                        btnConfirm?.visibility = View.VISIBLE
                        btnDeliver?.visibility = View.GONE
                        btnConfirm?.setOnClickListener {
                            listener.onConfirmOrder(order, position)
                        }
                    }
                    "confirmed" -> {
                        btnConfirm?.visibility = View.GONE
                        btnDeliver?.visibility = View.VISIBLE
                        btnDeliver?.setOnClickListener {
                            listener.onDeliverOrder(order, position)
                        }
                    }
                    else -> {
                        btnConfirm?.visibility = View.GONE
                        btnDeliver?.visibility = View.GONE
                    }
                }
            }
        }
    }
}