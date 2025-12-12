package com.iub.hometask.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.DocumentsContract

class GalleryRepository(private val context: Context) {

    suspend fun getAppPhotos(): List<Uri> = withContext(Dispatchers.IO) {
        val photoList = mutableListOf<Uri>()

        // Configuración de la consulta
        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        // Filtramos para buscar solo las fotos que contengan "Hometask" en su ruta o nombre
        // Nota: En Android 10+ (Scoped Storage), esto buscará principalmente lo que tu app creó
        // o lo que esté en la carpeta pública si tienes permiso de lectura.
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf("Hometask-App") // El nombre de la carpeta que definimos en UriUtils

        // Ordenar por fecha descendente (las nuevas primero)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            null, // Traemos todo y filtramos por carpeta si es necesario, o usamos selection si funciona bien en tu dispositivo
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(collection, id)
                photoList.add(contentUri)
            }
        }
        return@withContext photoList
    }

    // Función para borrar foto física
    suspend fun deletePhoto(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val rowsDeleted = context.contentResolver.delete(uri, null, null)
            return@withContext rowsDeleted > 0
        } catch (e: SecurityException) {
            // Requiere permisos especiales en Android 10+ si la foto no es nuestra
            e.printStackTrace()
            return@withContext false
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }


}
