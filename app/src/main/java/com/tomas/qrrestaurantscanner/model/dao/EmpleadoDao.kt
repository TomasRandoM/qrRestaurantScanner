package com.tomas.qrrestaurantscanner.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tomas.qrrestaurantscanner.model.entities.Empleado
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpleadoDao {
    @Query("SELECT * FROM empleados")
    fun getAll(): Flow<List<Empleado>>

    @Query("SELECT * FROM empleados WHERE id = :id")
    suspend fun getById(id: String): Empleado?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(empleados: Empleado)

    @Update
    suspend fun update(empleados: Empleado)

    @Delete
    suspend fun delete(empleados: Empleado)

    @Query("DELETE FROM empleados")
    suspend fun deleteAll()
}