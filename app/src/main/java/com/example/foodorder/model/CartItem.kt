package com.example.foodorder.model

data class CartItem(
    val id: Int = 0,
    val userId: Int = 0,
    val productId: Int = 0,
    var quantity: Int = 1,
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImage: String = ""
) {
    fun getSubtotal(): Double = productPrice * quantity
}