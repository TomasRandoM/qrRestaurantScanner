package com.tomas.qrrestaurantscanner.model.services

import com.tomas.qrrestaurantscanner.model.entities.Login
import com.tomas.qrrestaurantscanner.network.RetrofitClient

object LoginService {
    suspend fun login(username: String, password: String): String {
        val response = RetrofitClient.api.login(Login(username, password))
        if (!response.isSuccessful) {
            throw Exception("Error al iniciar sesión")
        }
        else {
            return response.body()?.accessToken ?: throw Exception("Error al iniciar sesión")
        }
    }
}