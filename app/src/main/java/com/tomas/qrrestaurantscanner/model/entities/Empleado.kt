package com.tomas.qrrestaurantscanner.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "empleados")
data class Empleado(
    val nombre: String = "",
    val apellido: String = "",
    @PrimaryKey val id: String = "",
)