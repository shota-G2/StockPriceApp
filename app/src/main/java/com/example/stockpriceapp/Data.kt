package com.example.stockpriceapp

data class Parameter(
    val mailaddress: String,
    val password: String
)

data class resultRefreshToken(
    val refreshToken: String
)

class RefreshTokenStock {
    lateinit var refreshToken: String
}