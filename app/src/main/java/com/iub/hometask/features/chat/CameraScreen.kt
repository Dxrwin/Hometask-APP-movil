package com.iub.hometask.features.chat

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.iub.hometask.utils.UriUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val previewView = remember { PreviewView(context) }

    // Efecto para vincular la cámara al ciclo de vida
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CameraScreen", "Error al iniciar cámara", e)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 2. Vista Previa
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Botón Cerrar
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(Icons.Default.Close, "Cerrar", tint = Color.White)
        }

        // 3. Controles de Cámara
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Cambiar Cámara (Módulo 12)
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                            CameraSelector.LENS_FACING_FRONT
                        else
                            CameraSelector.LENS_FACING_BACK
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(Icons.Default.Cameraswitch, "Cambiar", tint = Color.White)
                }

                // Botón Captura (Módulo 06)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(8.dp)
                        .background(Color.White, CircleShape)
                        .clickable {
                            // 4. Capturar Foto (Módulo 05)
                            imageCapture?.let { capture ->
                                takePhoto(context, capture) { file ->
                                    if (file != null) {
                                        // Devolver URI al Chat
                                        onImageCaptured(Uri.fromFile(file))
                                    }
                                }
                            }
                        }
                )

                // Espaciador para equilibrar
                Spacer(modifier = Modifier.size(50.dp))
            }
        }
    }
}

// Lógica de captura y guardado
private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoTaken: (File?) -> Unit
) {
    // Crear archivo con timestamp
    val photoFile = File(
        context.getExternalFilesDir(null), // Almacenamiento privado de la app
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d("CameraScreen", "Foto guardada: ${photoFile.absolutePath}")
                onPhotoTaken(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Error al guardar foto", exception)
                onPhotoTaken(null)
            }
        }
    )
}

// Extensión para obtener el proveedor de cámara de forma segura
private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener({
                continuation.resume(future.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }