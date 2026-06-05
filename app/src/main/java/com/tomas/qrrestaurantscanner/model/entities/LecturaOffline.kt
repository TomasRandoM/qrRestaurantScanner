package com.tomas.qrrestaurantscanner.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "lecturas")
data class LecturaOffline(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val empleadoId: String,
    val fecha: Date
)
