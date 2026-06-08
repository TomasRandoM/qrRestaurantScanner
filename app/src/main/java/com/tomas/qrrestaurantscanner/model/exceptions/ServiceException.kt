package com.tomas.qrrestaurantscanner.model.exceptions

class ServiceException(
    mensaje: String,
    causa: Throwable? = null
) : Exception(mensaje, causa)
