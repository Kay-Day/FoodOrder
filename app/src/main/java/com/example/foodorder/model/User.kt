package com.example.foodorder.model

data class User(
    val id: Int = 0,
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var phone: String = "",
    var address: String = "",
    var image: String = "",
    val userType: String = "customer" // "customer" hoặc "seller"
)