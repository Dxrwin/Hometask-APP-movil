package com.iub.hometask.features.gallery

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onBackClick: () -> Unit,
    onImageSelected: (Uri) -> Unit, // Si quieres devolver la imagen al chat
    viewModel: GalleryViewModel = viewModel(factory = GalleryViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estado para la vista detallada (Full Screen)
    var selectedImageForDetail by remember { mutableStateOf<Uri?>(null) }

    // Actualización automática al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadPhotos()
    }

    // Manejo del botón atrás nativo cuando está el detalle abierto
    BackHandler(enabled = selectedImageForDetail != null) {
        selectedImageForDetail = null
    }

    Scaffold(
        topBar = {
            if (selectedImageForDetail == null) {
                TopAppBar(
                    title = { Text("Galería Hometask") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            // CONTENIDO PRINCIPAL
            when (val state = uiState) {
                is GalleryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is GalleryUiState.Empty -> {
                    EmptyStateIndicator()
                }
                is GalleryUiState.Success -> {
                    // GRID RESPONSIVE (Requisito cumplido)
                    LazyVerticalGrid(
                        // Adaptive: Crea tantas columnas como quepan con mínimo 128dp
                        columns = GridCells.Adaptive(minSize = 128.dp),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.photos) { uri ->
                            PhotoGridItem(
                                uri = uri,
                                onClick = { selectedImageForDetail = uri } // Abre detalle
                            )
                        }
                    }
                }
            }

            // VISTA DETALLADA (Overlay)
            if (selectedImageForDetail != null) {
                DetailImageView(
                    uri = selectedImageForDetail!!,
                    onClose = { selectedImageForDetail = null },
                    onSelect = {
                        onImageSelected(selectedImageForDetail!!) // Seleccionar para enviar
                    }
                )
            }
        }
    }
}

@Composable
fun PhotoGridItem(uri: Uri, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f) // Cuadrado perfecto
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
            error = painterResource(id = android.R.drawable.ic_menu_report_image) // Importa si falta
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

@Composable
fun DetailImageView(uri: Uri, onClose: () -> Unit, onSelect: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(enabled = false) {} // Evita clicks al fondo
    ) {
        // Imagen Zoom (Coil carga tamaño completo)
        AsyncImage(
            model = uri,
            contentDescription = "Detalle",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // Botón Cerrar
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Cerrar",
                tint = Color.White
            )
        }

        // Botón Seleccionar/Enviar (Opcional)
        Button(
            onClick = onSelect,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("Enviar Foto")
        }
    }
}