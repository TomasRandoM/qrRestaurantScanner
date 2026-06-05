package com.tomas.qrrestaurantscanner.model.services

import android.content.Context
import android.util.Log
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

    suspend fun validateQR(context: Context, value: String, qrkey: String?): String {
        if (qrkey != null) {
            val parts = value.split(",")
            if (parts.size != 2) {
                throw Exception("El QR no es válido")
            }
            val key = parts[0]
            val employeeId = parts[1]
            if (key != qrkey) {
                throw Exception("La key es inválida. Intente conectar su teléfono a internet para actualizarla")
            }
            LecturaService().checkEmpleadoEntry(context, employeeId)
            return "ENTRADA ACEPTADA"
        }
        else {
            throw Exception("La key es inválida. Intente conectar su teléfono a internet para actualizarla")
        }
    }
}