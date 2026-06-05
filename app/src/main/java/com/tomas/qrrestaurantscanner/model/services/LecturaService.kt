package com.tomas.qrrestaurantscanner.model.services

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import com.tomas.qrrestaurantscanner.model.database.AppDatabase
import com.tomas.qrrestaurantscanner.model.entities.LecturaOffline
import com.tomas.qrrestaurantscanner.network.RetrofitClient
import com.tomas.qrrestaurantscanner.storage.Storage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import okio.IOException

class LecturaService {
    suspend fun checkEmpleadoEntry(context: Context, empleadoId: String) {
        try {
            RetrofitClient.api.checkEntry(empleadoId)
        }
        catch (e: IOException) {
            Storage(context).storeInternet(false);
            val db = AppDatabase.getInstance(context)
            val dao = db.lecturaOfflineDao()
            dao.insert(LecturaOffline(0, empleadoId, java.util.Date()))
        }
    }

    suspend fun checkEmpleadoOfflineEntries(context: Context) {
        try {
            if (!Storage(context).getInternet()) {
                val db = AppDatabase.getInstance(context)
                val dao = db.lecturaOfflineDao()
                dao.getAll().collect() {lecturas ->
                    if (!lecturas.isEmpty()) {
                        RetrofitClient.api.sync(lecturas)
                    }
                }
            }
        }
        catch (e: IOException) {
            Storage(context).storeInternet(false);
        }
        catch (e: Exception) {

        }
    }
}