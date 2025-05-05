package com.example.foodorder.model

data class Order(
    val id: Int = 0,
    val userId: Int = 0,
    val date: String = "",
    val total: Double = 0.0,
    var status: String = "placed" // "placed", "confirmed", "delivered"
)