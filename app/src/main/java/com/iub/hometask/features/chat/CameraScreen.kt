package com.iub.hometask.features.chat

import android.content.Context
import android.net.Uri
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
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit, // Callback cuando se toma la foto
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Estados de CameraX
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }

    // Efecto para vincular la cámara al ciclo de vida
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Error iniciando cámara", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 1. VISTA PREVIA (CÁMARA EN TIEMPO REAL)
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Botón Cerrar (Arriba Izquierda)
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Cerrar", tint = Color.White)
        }

        // 2. CONTROLES (ABAJO)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón vacío para equilibrar
            Spacer(modifier = Modifier.size(50.dp))

            // 3. BOTÓN DE CAPTURA (Estilo Visual Personalizado)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(4.dp, Color.White, CircleShape)
                    .clickable {
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            onImageSaved = onImageCaptured,
                            onError = { Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show() }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White, CircleShape)
                )
            }

            // Botón Cambiar Cámara
            IconButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                        CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK
                },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(Icons.Default.Cameraswitch, "Cambiar Cámara", tint = Color.White)
            }
        }
    }
}

// Lógica de captura y guardado
private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageSaved: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    // 4. GUARDADO AUTOMÁTICO EN GALERÍA (REQUIREMENT)
    val outputOptions = UriUtils.createMediaStoreImageOptions(context)

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri
                if (savedUri != null) {
                    // 5. FEEDBACK VISUAL
                    Toast.makeText(context, "Foto guardada en Galería", Toast.LENGTH_SHORT).show()
                    onImageSaved(savedUri)
                }
            }

            override fun onError(exc: ImageCaptureException) {
                onError(exc.message ?: "Error desconocido")
            }
        }
    )
}

// Extensión para obtener el proveedor de cámara de forma segura
private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener({
        continuation.resume(cameraProviderFuture.get())
    }, ContextCompat.getMainExecutor(this))
}