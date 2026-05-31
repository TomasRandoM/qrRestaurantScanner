package com.tomas.qrrestaurantscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tomas.qrrestaurantscanner.scanner.camera.CameraManager

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private var lastScannedValue: String? = null

    //Pide el permiso para la camara
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                cameraManager.start()
            } else {
                Toast.makeText(this, "Necesita dar permisos de camara", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val previewView = findViewById<PreviewView>(R.id.previewView)
        cameraManager = CameraManager(
            context = this,
            lifecycleOwner = this,
            previewView = previewView,
            onQrDetected = ::onQrDetected
        )
        if (hasCameraPermission()) {
            cameraManager.start()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private fun hasCameraPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    private fun onQrDetected(value: String) {
        //Aca se filtran las lecturas repetidas (porque se van leyendo muy rapido)
        if (value == lastScannedValue) return
        lastScannedValue = value
        runOnUiThread {
            Toast.makeText(this, "QR: $value", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraManager.shutdown()
    }
}
