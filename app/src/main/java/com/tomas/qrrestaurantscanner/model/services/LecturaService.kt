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
import okhttp3.Response
import okio.IOException

class LecturaService {
    suspend fun checkEmpleadoEntry(context: Context, empleadoId: String) {
        try {
            val response = RetrofitClient.api.checkEntry(empleadoId)
            if (!response.isSuccessful) {
                Log.d("Internet", "No llega")
                throw IOException("Error")
            }
        }
        catch (e: IOException) {
            Log.d("Internet", "Guardado")
            Storage(context).storeInternet(false);
            val db = AppDatabase.getInstance(context)
            val dao = db.lecturaOfflineDao()
            dao.insert(LecturaOffline(0, empleadoId, java.util.Date()))
        }
    }

    suspend fun checkEmpleadoOfflineEntries(context: Context) {
        try {
            if (!Storage(context).getInternet()) {
                Log.d("Internet", "Entrante")
                val db = AppDatabase.getInstance(context)
                val dao = db.lecturaOfflineDao()
                val lecturas = dao.getAll()
                if (lecturas.isNotEmpty()) {
                    RetrofitClient.api.sync(lecturas)
                }
                Storage(context).storeInternet(true)
                dao.deleteAll()

            }
        }
        catch (e: IOException) {
            Storage(context).storeInternet(false);
        }
        catch (e: Exception) {
        }
    }
}