package com.tomas.qrrestaurantscanner.model.services

import com.tomas.qrrestaurantscanner.model.entities.Empleado
import com.tomas.qrrestaurantscanner.model.entities.QRSecrets
import com.tomas.qrrestaurantscanner.network.RetrofitClient

class QRService {
    suspend fun updateQRKey(idEmpleado: String): QRSecrets {
        val response = RetrofitClient.api.getQRKey(idEmpleado)
        if (response.isSuccessful) {
            return response.body()?: throw Exception("Error al obtener el QRKey")
        } else {
            throw Exception("Error al actualizar el QRKey")
        }
    }
}