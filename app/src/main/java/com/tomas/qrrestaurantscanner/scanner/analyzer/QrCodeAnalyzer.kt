package com.tomas.qrrestaurantscanner.scanner.analyzer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

//Recibe una funcion como parámetro
class QrCodeAnalyzer(private val onQrDetected: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    //ImageProxy es un objeto que tiene la foto y los metadatos
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        //Se transforma el formato de la imagen, rotationDegrees para que lea el QR en la orientacion correcta
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                //agarra el primer elemento si existe o null si no, si no es null se accede al rawValue, y si este no es null se ejecuta la funcion pasada como parametro
            barcodes.firstOrNull()?.rawValue?.let {  value ->
                onQrDetected(value)
            }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
            .addOnCompleteListener {
                //cameraX tiene un buffer limitado, entonces hay que liberarlo para que pueda seguir siendo usado
                imageProxy.close()
            }
    }

}
