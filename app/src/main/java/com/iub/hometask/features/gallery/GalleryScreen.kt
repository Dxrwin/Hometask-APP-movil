package com.iub.hometask.features.gallery

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onNavigateBack: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    viewModel: GalleryViewModel = viewModel(factory = GalleryViewModel.Factory)
) {
    val context = LocalContext.current
    var photos by remember { mutableStateOf<List<File>>(emptyList()) }

    // Cargar fotos al iniciar (Módulo 07)
    LaunchedEffect(Unit) {
        photos = loadPhotos(context)
    }

    /*BackHandler(enabled = selectedPhotoIndex != null) {
        selectedPhotoIndex = null
    }*/

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Galería App") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (photos.isEmpty()) {
            EmptyGalleryMessage(modifier = Modifier.padding(paddingValues))
        } else {
            // Grid de Fotos (Módulo 07)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 Columnas
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(photos) { photo ->
                    AsyncImage(
                        model = photo,
                        contentDescription = "Foto",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                // Al hacer click, devolvemos la URI al chat
                                onImageSelected(Uri.fromFile(photo))
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
    }

    // Función para cargar fotos del directorio privado (Módulo 07)
    private fun loadPhotos(context: Context): List<File> {
        val directory = context.getExternalFilesDir(null) // /data/data/pkg/files/
        return directory?.listFiles { file ->
            file.extension.lowercase() in listOf("jpg", "jpeg", "png")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }


@Composable
fun EmptyGalleryMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("No hay fotos tomadas con esta app", color = Color.Gray)
        }
    }
}

// --- COMPONENTES VISUALES ---

@Composable
fun PhotoGridItem(uri: Uri, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            contentDescription = "Foto",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            // Usa un icono del sistema si no tienes uno propio
            error = painterResource(id = android.R.drawable.ic_menu_report_image)
        )
    }
}

@Composable
fun EmptyStateIndicator() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PhotoLibrary,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("No hay fotos aún", color = Color.Gray)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailImageView(
    photos: List<Uri>,
    initialIndex: Int,
    onClose: () -> Unit,
    onSelect: (Uri) -> Unit,
    onDelete: (Uri) -> Unit
) {
    // Usamos el pager state inicializado en el índice seleccionado
    val pagerState = rememberPagerState(initialPage = initialIndex) { photos.size }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Obtenemos la URI actual dinámicamente según la página del pager
    val currentUri = if (pagerState.currentPage < photos.size) photos[pagerState.currentPage] else null

    // DIÁLOGO DE CONFIRMACIÓN
    if (showDeleteDialog && currentUri != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar foto?") },
            text = { Text("Esta foto se borrará permanentemente de tu dispositivo.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(currentUri)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    // Opcional: Mostrar "1 / 10"
                    Text(
                        "${pagerState.currentPage + 1} / ${photos.size}",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { currentUri?.let { onSelect(it) } },
                    enabled = currentUri != null
                ) {
                    Text("Enviar esta Foto")
                }
            }
        }
    ) { padding ->
        // VISOR DE FOTOS (SWIPEABLE)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            pageSpacing = 16.dp
        ) { page ->
            AsyncImage(
                model = photos[page],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}