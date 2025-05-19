package com.example.models

data class Book(
    var id: String = "",
    var title: String = "",
    var author: String = "",
    var description: String = "",
    var imagePath: String = "",
    var copies: Int = 0
)