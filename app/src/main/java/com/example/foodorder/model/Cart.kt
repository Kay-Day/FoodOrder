package com.example.foodorder.model

data class Cart(
    val id: Int = 0,
    val userId: Int = 0,
    val productId: Int = 0,
    var quantity: Int = 1
)