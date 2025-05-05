package com.example.foodorder.model

data class OrderDetail(
    val id: Int = 0,
    val orderId: Int = 0,
    val productId: Int = 0,
    val quantity: Int = 0,
    val price: Double = 0.0
) {
    fun getSubtotal(): Double = price * quantity
}