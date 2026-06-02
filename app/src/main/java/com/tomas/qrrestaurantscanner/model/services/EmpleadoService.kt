package com.tomas.qrrestaurantscanner.model.services

import com.tomas.qrrestaurantscanner.model.entities.Empleado
import com.tomas.qrrestaurantscanner.network.RetrofitClient

class EmpleadoService {
    suspend fun getAllEmpleados(): List<Empleado> {
        val response = RetrofitClient.api.getAllEmpleados()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Error al obtener empleados")
        }
    }

    suspend fun getEmpleadoIdByEmail(email: String): String {
        val response = RetrofitClient.api.getEmpleadoByEmail(email)
        if (response.isSuccessful) {
            print(response.body())
            return response.body()?.id?: throw Exception("Error al obtener el empleado")
        } else {
            throw Exception("Error al obtener empleado")
        }
    }

    suspend fun saveEmpleados() {
        val empleados = getAllEmpleados()

    }
}