package com.iub.hometask.features.chat

import android.net.Uri
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.iub.hometask.data.repository.ChatMessage
import com.iub.hometask.data.repository.MemberUiModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.iub.hometask.data.repository.MessageStatus
import com.iub.hometask.navigation.Routes
import com.iub.hometask.utils.UriUtils



private val DarkBackground = Color(0xFF121212)
private val CardBackground = Color(0xFF1E1E1E)
private val MyMessageColor = Color(0xFF005c4b) // Verde oscuro tipo WhatsApp
private val OtherMessageColor = Color(0xFF202c33)
private val TextWhite = Color(0xFFEEEEEE)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    member: MemberUiModel,
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit,
    navController: androidx.navigation.NavController,
    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)
) {

    // --- ESTADOS LOCALES ---

    // Imagen pendiente de envío (Borrador)
    val pendingImageUri = remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val capturedImageFlow = remember(navController.currentBackStackEntry) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<Uri?>("captured_image_uri", null)
    }

    val capturedUriState = capturedImageFlow?.collectAsState()
    val capturedUri = capturedUriState?.value

    LaunchedEffect(capturedUri) {
        capturedUri?.let { uri ->
            // EN LUGAR DE ENVIAR, LO PONEMOS EN PREVIEW
            pendingImageUri.value = uri
            // Limpiamos el handle para no repetirlo
            navController.currentBackStackEntry?.savedStateHandle?.remove<Uri>("captured_image_uri")
        }
    }

    // 2. Retorno de GALERÍA (Viene de nuestra GalleryScreen)
    val galleryFlow = remember(navController.currentBackStackEntry) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<Uri?>("gallery_selected_uri", null)
    }
    val galleryUriState = galleryFlow?.collectAsState()
    val galleryUri = galleryUriState?.value

    LaunchedEffect(galleryUri) {
        galleryUri?.let { uri ->
            // EN LUGAR DE ENVIAR, LO PONEMOS EN PREVIEW
            pendingImageUri.value = uri
            navController.currentBackStackEntry?.savedStateHandle?.remove<Uri>("gallery_selected_uri")
        }
    }

    val capturedImageState = capturedImageFlow?.collectAsState() ?: remember { mutableStateOf(null)}
    val capturedImageUri by remember { derivedStateOf { capturedImageState.value } }
    val uiState by viewModel.messagesState.collectAsState()
    val messageText = remember { mutableStateOf("") }

    val gallerySelectedUri by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<Uri?>("gallery_selected_uri", null)
        ?.collectAsState()
        ?: remember { mutableStateOf(null) }

    // Lógica para subir la foto cuando cambia la selección
    LaunchedEffect(gallerySelectedUri) {
        gallerySelectedUri?.let { uri ->
            viewModel.uploadAndSendImageWithContext(uri, context)
            // Limpiamos el estado para no reenviar si rotas la pantalla
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Uri>("gallery_selected_uri")
        }
    }




    // --- LÓGICA DE CÁMARA Y GALERÍA ---

    LaunchedEffect(gallerySelectedUri) {
        gallerySelectedUri?.let { uri ->
            viewModel.uploadAndSendImage(uri, context)
            // Limpiar estado
            navController.currentBackStackEntry?.savedStateHandle?.remove<Uri>("gallery_selected_uri")
        }
    }

    LaunchedEffect(capturedImageUri) {
        capturedImageUri?.let { uri ->
            viewModel.uploadAndSendImage(uri, context)
            // Limpiamos para no reenviar si rota la pantalla
            savedStateHandle?.remove<Uri>("captured_image_uri")
        }
    }

    // 1. Estado para guardar la URI temporal de la foto que vamos a tomar
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // 2. Launcher de GALERÍA (Seleccionar foto)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Si el usuario seleccionó algo, lo enviamos
        uri?.let { viewModel.uploadAndSendImage(it, context) }
    }

    // 3. Launcher de CÁMARA (Tomar foto)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            // Si la foto se tomó bien, la enviamos usando la URI temporal
            viewModel.uploadAndSendImage(tempPhotoUri!!, context)
        }
    }

    // 4. Gestión de Permiso de Cámara
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    // Función auxiliar para iniciar el proceso de cámara
    fun launchCamera() {
        if (cameraPermissionState.status.isGranted) {
            // A. Crear archivo temporal
            val file = UriUtils.createTempImageFile(context)
            // B. Obtener URI segura
            val uri = UriUtils.getUriForFile(context, file)
            tempPhotoUri = uri
            // C. Lanza la cámara
            cameraLauncher.launch(uri)
        } else {
            // Si no hay permiso, pedirlo
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Cargar mensajes al entrar
    LaunchedEffect(member.id) {
        // CORRECCIÓN 2: Llamamos a 'loadChatData' que es como se llama en el VM ahora
        viewModel.loadChatData(member.id)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar Pequeño
                        if (member.imageUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(member.imageUrl).crossfade(true).build(),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, null, tint = TextWhite)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(member.name, color = TextWhite)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            // Barra de Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                ChatInputBar(
                    messageText = messageText.value,
                    onMessageChange = { messageText.value = it },
                    pendingImageUri = pendingImageUri.value,
                    onRemoveImage = { pendingImageUri.value = null }, // Acción Cancelar
                    onCameraClick = { launchCamera() },
                    onGalleryClick = { navController.navigate(Routes.GALLERY) },
                    onSend = {
                        // LÓGICA DE ENVÍO UNIFICADA
                        if (pendingImageUri.value != null) {
                            // Enviar Imagen + Texto
                            viewModel.uploadAndSendImageWithContext(
                                uri = pendingImageUri.value!!,
                                context = context,
                                caption = messageText.value // Pasamos el texto como comentario
                            )
                        } else {
                            // Solo Texto
                            viewModel.sendMessage(messageText.value)
                        }

                        // Limpiar todo después de enviar
                        messageText.value = ""
                        pendingImageUri.value = null
                    }
                )

                // Botón CÁMARA
                IconButton(onClick = { launchCamera() },
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, "Cámara", tint = Color.Gray)
                }

                // Botón GALERÍA
                IconButton(onClick = { navController.navigate(Routes.GALLERY) },
                    modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                    Icon(Icons.Default.Image, "Galería", tint = Color.Gray)
                }

                OutlinedTextField(
                    value = messageText.value,
                    onValueChange = { messageText.value = it },
                    placeholder = { Text("Mensaje...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                // Botón ENVIAR
                IconButton(
                    onClick = {
                        viewModel.sendMessage(messageText.value)
                        messageText.value = ""
                    },
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is ChatUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ChatUiState.Error -> Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                is ChatUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true // Para chats es mejor que empiece abajo
                    ) {
                        // Invertimos la lista porque reverseLayout=true la muestra al revés
                        items(state.messages.reversed()) { msg: ChatMessage ->
                            ChatBubble(msg)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {

    val isMine = message.isMine
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMine) MyMessageColor else OtherMessageColor
    val shape = if (isMine) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (message.isMine) MyMessageColor else OtherMessageColor
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Mostrar Imagen si existe
                if (message.imageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(message.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen enviada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Hora y Estado
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = try { message.timestamp.takeLast(8) } catch(e:Exception){""},
                        color = Color.LightGray,
                        fontSize = 10.sp
                    )
                    if (isMine) {
                        Spacer(modifier = Modifier.width(4.dp))

                        when (message.status) {
                            MessageStatus.SENDING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = TextWhite
                                )
                            }
                            MessageStatus.FAILED -> {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Red
                                )
                            }
                            MessageStatus.SENT -> {

                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Leído",
                            modifier = Modifier.size(16.dp),
                            tint = if (message.isRead) Color(0xFF34B7F1) else Color.Gray
                        )
                    }
                }
            }

        }
    }
}
}

}

@Composable
fun ChatInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    pendingImageUri: Uri?, // <--- Estado de la imagen "en espera"
    onRemoveImage: () -> Unit, // Cancelar imagen
    onSend: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground) // Color de fondo del área de input
    ) {
        // 1. ÁREA DE PREVISUALIZACIÓN (Solo visible si hay imagen)
        if (pendingImageUri != null) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(150.dp) // Altura de la previsualización
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pendingImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Previsualización",
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                // Botón CANCELAR (X)
                IconButton(
                    onClick = onRemoveImage,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(0.6f), CircleShape)
                        .size(32.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar adjunto",
                        tint = Color.White
                    )
                }
            }
        }

        // 2. FILA DE BOTONES Y TEXTO
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom // Alineado abajo para multilínea
        ) {
            // Botones Multimedia
            IconButton(onClick = onCameraClick, modifier = Modifier.padding(bottom = 4.dp)) {
                Icon(Icons.Default.CameraAlt, "Cámara", tint = Color.Gray)
            }
            IconButton(onClick = onGalleryClick, modifier = Modifier.padding(bottom = 4.dp)) {
                Icon(Icons.Default.Image, "Galería", tint = Color.Gray)
            }

            // Campo de Texto
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                placeholder = {
                    Text(
                        if (pendingImageUri != null) "Añade un comentario..." else "Mensaje...",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkBackground,
                    unfocusedContainerColor = DarkBackground,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                maxLines = 4
            )

            // Botón Enviar
            val canSend = messageText.isNotBlank() || pendingImageUri != null
            IconButton(
                onClick = onSend,
                enabled = canSend,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .background(
                        if (canSend) MaterialTheme.colorScheme.primary else Color.Gray,
                        CircleShape
                    )
                    .size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = Color.White)
            }
        }
    }
}