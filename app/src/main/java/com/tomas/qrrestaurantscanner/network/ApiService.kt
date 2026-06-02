package com.tomas.qrrestaurantscanner.network

import com.tomas.qrrestaurantscanner.model.entities.Empleado
import com.tomas.qrrestaurantscanner.model.entities.Login
import com.tomas.qrrestaurantscanner.model.entities.QRSecrets
import com.tomas.qrrestaurantscanner.model.entities.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("api/v1/empleado")
    suspend fun getAllEmpleados(): Response<List<Empleado>>

    @GET("api/v1/qr/{id}")
    suspend fun getQRKey(@Path("id") id: String): Response<QRSecrets>

    @POST("login")
    suspend fun login(@Body body: Login): Response<Token>

    @GET("api/v1/empleado/email/{correo}")
    suspend fun getEmpleadoByEmail(@Path("correo") correo: String): Response<Empleado>
}
