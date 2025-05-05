package com.example.foodorder.model

data class Product(
    val id: Int = 0,
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var image: String = "",
    var category: String = "", // "food" hoặc "drink"
    val sellerId: Int = 0,
    var quantitySold: Int = 0
)