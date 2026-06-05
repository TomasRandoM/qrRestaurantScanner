package com.tomas.qrrestaurantscanner

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomas.qrrestaurantscanner.model.services.EmpleadoService
import com.tomas.qrrestaurantscanner.model.services.LecturaService
import com.tomas.qrrestaurantscanner.model.services.LoginService
import com.tomas.qrrestaurantscanner.model.services.QRService
import com.tomas.qrrestaurantscanner.network.RetrofitClient
import com.tomas.qrrestaurantscanner.storage.Storage
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val context = LocalContext.current
                    val token: String? = Storage(context).getToken()
                    val scope = rememberCoroutineScope()
                    if (token != null) {
                        RetrofitClient.recreateApiService(token)
                        scope.launch {
                            try {
                                //Si hay conexion, actualiza el QRKey
                                val id = Storage(context).getId()
                                val key = QRService().updateQRKey(id!!)
                                Storage(context).storeQrKey(key.qrKey)
                                EmpleadoService().saveEmpleados(context)
                                LecturaService().checkEmpleadoOfflineEntries(context)
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                            } catch (e: java.io.IOException) {
                                //Si no hay conexion, entra con las credenciales existentes
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                //continua a la pestaña de login
                            }
                        }

                    }
                    RegisterScreen()
                }
            }
        }
    }
}

@Composable
private fun RegisterScreen() {
    val context = LocalContext.current
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar sesión",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                error = null
                scope.launch {
                    try {
                        val token = LoginService.login(user, password)
                        Storage(context).storeToken(token)
                        RetrofitClient.recreateApiService(token)
                        val id = EmpleadoService().getEmpleadoIdByEmail(user)
                        Storage(context).storeId(id)
                        val key = QRService().updateQRKey(id)
                        Storage(context).storeQrKey(key.qrKey)
                        EmpleadoService().saveEmpleados(context);
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        error = "Error de inicio de sesión. Reintente"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Enviar",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error!!,
                color = Color.Red,
                fontSize = 14.sp,
            )
        }
    }
}
