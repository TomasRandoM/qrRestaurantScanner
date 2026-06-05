package com.tomas.qrrestaurantscanner.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tomas.qrrestaurantscanner.model.dao.EmpleadoDao
import com.tomas.qrrestaurantscanner.model.dao.LecturaOfflineDao
import com.tomas.qrrestaurantscanner.model.entities.Empleado
import com.tomas.qrrestaurantscanner.model.entities.LecturaOffline

@Database(entities = [LecturaOffline::class, Empleado::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lecturaOfflineDao(): LecturaOfflineDao
    abstract fun empleadoDao(): EmpleadoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
