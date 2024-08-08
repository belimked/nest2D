package com.nestapp

class Configuration(
    val baseUrl: String,
    val appVersion: String,
    val endpoint: String,
    val port: Int,
    val accessKey: String,
    val secretKey: String,
    val mongoUrl: String,
)

const val TOLERANCE = 1e-2
