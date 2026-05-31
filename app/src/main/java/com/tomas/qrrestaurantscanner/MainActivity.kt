package com.tomas.qrrestaurantscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.tomas.qrrestaurantscanner.scanner.camera.CameraManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // El activity actual es el lifecycleOwner
                    QrScannerScreen(lifecycleOwner = this)
                }
            }
        }
    }
}
@Composable
private fun QrScannerScreen(lifecycleOwner: LifecycleOwner) {
    val context = LocalContext.current
    var scannedValue by remember { mutableStateOf<String?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    //Pide el permiso de la camara
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }
    //Si no hay permiso, se solicita nuevamente
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            //Si tenemos permiso de camara, componemos la vista de la camara
            CameraPreview(
                lifecycleOwner = lifecycleOwner,
                //Cuando se detecte un QR, se actualizará el valor y se colocará en scannedValue
                onQrDetected = { value ->
                    if (value != scannedValue) {
                        scannedValue = value
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            //Si el scannedValue no es nulo, se renderiza la vista con el texto del QR
            scannedValue?.let { value ->
                Text(
                    text = "QR: $value",
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(color = Color.Yellow)
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        } else {
            Text(
                text = "Necesita dar permisos de camara",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            )
        }
    }
}

//CameraX usa la vista de android clasico, entonces se adapta
@Composable
private fun CameraPreview(
    lifecycleOwner: LifecycleOwner,
    onQrDetected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    //El PreviewView es el mismo que en android clasico pero lo adaptamos a compose usando un remember
    val previewView = remember { PreviewView(context) }
    //igual para el CameraManager (que no se recomponga si no que se utilice el mismo)
    val cameraManager = remember {
        CameraManager(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            onQrDetected = onQrDetected
        )
    }
    //Al componerse el componente inicia la camara y al cerrarse se elimina
    DisposableEffect(Unit) {
        cameraManager.start()
        onDispose { cameraManager.shutdown() }
    }

    //AndroidView permite usar un componente de android clasico en compose
    AndroidView(factory = { previewView }, modifier = modifier)
}
