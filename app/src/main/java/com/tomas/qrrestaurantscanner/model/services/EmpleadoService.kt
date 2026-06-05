package com.tomas.qrrestaurantscanner.model.services

import android.content.Context
import com.tomas.qrrestaurantscanner.model.database.AppDatabase
import com.tomas.qrrestaurantscanner.model.entities.Empleado
import com.tomas.qrrestaurantscanner.model.entities.LecturaOffline
import com.tomas.qrrestaurantscanner.network.ApiService
import com.tomas.qrrestaurantscanner.network.RetrofitClient
import com.tomas.qrrestaurantscanner.storage.Storage
import okio.IOException

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

    suspend fun saveEmpleados(context: Context) {
        val empleados = getAllEmpleados()
        val db = AppDatabase.getInstance(context)
        val dao = db.empleadoDao()
        empleados.forEach {
            empleado -> dao.insert(empleado)
        }
    }

}