package com.tomas.qrrestaurantscanner.model.dao

import androidx.room.*
import com.tomas.qrrestaurantscanner.model.entities.LecturaOffline
import kotlinx.coroutines.flow.Flow

@Dao
interface LecturaOfflineDao {

    @Query("SELECT * FROM lecturas")
    suspend fun getAll(): List<LecturaOffline>

    @Query("SELECT * FROM lecturas WHERE id = :id")
    suspend fun getById(id: String): LecturaOffline?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lecturas: LecturaOffline)

    @Update
    suspend fun update(lecturas: LecturaOffline)

    @Delete
    suspend fun delete(lecturas: LecturaOffline)

    @Query("DELETE FROM lecturas")
    suspend fun deleteAll()
}
