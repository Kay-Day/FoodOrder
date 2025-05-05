package com.example.foodorder.model

data class Notification(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String = "",
    val message: String = "",
    val date: String = "",
    var isRead: Boolean = false
)