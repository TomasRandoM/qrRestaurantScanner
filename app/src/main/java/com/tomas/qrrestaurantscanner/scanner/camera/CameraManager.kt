package com.tomas.qrrestaurantscanner.scanner.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.tomas.qrrestaurantscanner.scanner.analyzer.QrCodeAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val onQrDetected: (String) -> Unit
) {

    // Hilo donde se corre el analisis de cada frame
    private val analysisExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun start() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            bindUseCases(cameraProviderFuture.get())
        }, ContextCompat.getMainExecutor(context))
    }
    //CameraProvider es el objeto que maneja la camara fisica automaticamente
    private fun bindUseCases(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val imageAnalysis = ImageAnalysis.Builder()
            //El keep only latest es por si se atrasa, si eso pasa se usa solo la lectura mas reciente
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(analysisExecutor, QrCodeAnalyzer(onQrDetected))
            }
        try {
            cameraProvider.unbindAll()
            //esto hace que cameraX se administre solo, la camara se abre cuando la actividad esta en resumed
            //y se cierra cuando se pausa o se destruye
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun shutdown() {
        analysisExecutor.shutdown()
    }
}
