package com.iub.hometask.utils

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UriUtils {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 1. CONFIGURACIÓN PARA GUARDAR (Ya la tenías, asegúrate que esté así)
    fun createMediaStoreImageOptions(context: Context): androidx.camera.core.ImageCapture.OutputFileOptions {
        val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$name")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                // Carpeta específica para tu App
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Hometask-App")
            }
        }

        return androidx.camera.core.ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()
    }


    // 2. NUEVA FUNCIÓN: Forzar actualización de la galería
    fun refreshGallery(context: Context, uri: Uri) {
        // Obtenemos la ruta real (si es posible) o simplemente escaneamos el archivo
        // Nota: Con MediaStore y URIs 'content://', a veces el sistema ya lo sabe,
        // pero esto fuerza a que otras apps (y nuestra query) lo vean.

        try {
            // Un truco para forzar el refresco es usar MediaScannerConnection
            // aunque tengamos una URI de contenido.
            val path = getFileFromUri(context, uri)?.absolutePath
            if (path != null) {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(path),
                    arrayOf("image/jpeg"),
                    null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // Crea un archivo temporal vacío donde la cámara guardará la foto
    fun createTempImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        return File.createTempFile(
            imageFileName,
            ".jpg",
            context.externalCacheDir // Usamos cache externo para que la cámara pueda escribir
        )
    }

    // Obtiene la URI segura (FileProvider) para ese archivo temporal
    fun getUriForFile(context: Context, file: File): Uri {
        // IMPORTANTE: Debes configurar el FileProvider en el Manifest
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // Authority definido en Manifest
            file
        )
    }


}