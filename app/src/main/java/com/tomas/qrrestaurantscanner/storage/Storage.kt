package com.tomas.qrrestaurantscanner.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class Storage(context: Context) {
    private val prefs = context.getSharedPreferences("login", MODE_PRIVATE)
    fun storeToken(token: String) {
        prefs?.edit() { putString("token", token)}
    }

    fun storeId(id: String) {
        prefs?.edit() { putString("id", id)}
    }

    fun storeQrKey(key: String) {
        prefs?.edit() { putString("qrKey", key)}
    }

    fun getToken(): String? {
        val token: String? = prefs?.getString("token", null)
        return token
    }

    fun getId(): String? {
        val id: String? = prefs?.getString("id", null)
        return id
    }

    fun storeInternet(internet: Boolean) {
        prefs?.edit() { putBoolean("internet", internet)}
    }

    fun getInternet(): Boolean {
        val internet: Boolean = prefs.getBoolean("internet", true)
        return internet
    }

    fun getQrKey(): String? {
        val qrKey: String? = prefs?.getString("qrKey", null)
        return qrKey
    }
}