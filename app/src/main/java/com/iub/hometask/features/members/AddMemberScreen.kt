package com.iub.hometask.features.members

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iub.hometask.features.auth.Signup.SignUpUiState
import com.iub.hometask.ui.components.*

// Definimos colores oscuros locales por si el tema no los carga bien
private val DarkBackground = Color(0xFF121212) // Fondo casi negro
private val DarkSurface = Color(0xFF1E1E1E)    // Fondo de tarjetas/barras
private val WhiteText = Color(0xFFEEEEEE)      // Texto blanco

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onBackClick: () -> Unit,
    onMemberCreated: () -> Unit,
    viewModel: AddMemberViewModel = viewModel(factory = AddMemberViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Campos
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Selectores (Dropdowns)
    val roles = listOf("Administrador" to 1, "Miembro" to 2)
    var selectedRole by remember { mutableStateOf(roles[1]) }
    var expandedRole by remember { mutableStateOf(false) }

    val homes = listOf("Hogar Principal (1)" to 1, "Hogar Secundario (2)" to 2)
    var selectedHome by remember { mutableStateOf(homes[0]) }
    var expandedHome by remember { mutableStateOf(false) }

    // Efectos de UI (Éxito / Error)
    LaunchedEffect(uiState) {
        when(uiState) {
            is SignUpUiState.Success -> {
                snackbarHostState.showSnackbar("Miembro creado exitosamente")
                onMemberCreated()
            }
            is SignUpUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as SignUpUiState.Error).message)
            }
            else -> {}
        }
    }

    // ESTRUCTURA VISUAL EN MODO OSCURO
    Scaffold(
        // 1. FORZAMOS EL COLOR DE FONDO OSCURO
        containerColor = DarkBackground,

        topBar = {
            TopAppBar(
                title = { Text("Nuevo Miembro", color = WhiteText) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = WhiteText // Ícono blanco
                        )
                    }
                },
                // 2. FORZAMOS COLORES DE LA BARRA
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = WhiteText,
                    navigationIconContentColor = WhiteText
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Scroll habilitado
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Formulario
            // Nota: HomeTaskTextField ya debería tener los colores corregidos (TextPrimary)
            // Si TextPrimary es blanco, se verá perfecto sobre este fondo oscuro.

            LabeledField("Nombre Completo") {
                HomeTaskTextField(
                    value = fullName.value,
                    onValueChange = { fullName.value = it },
                    placeholder = "Ej: Juan Pérez"
                )
            }

            LabeledField("Correo Electrónico") {
                HomeTaskTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    placeholder = "usuario@email.com"
                )
            }

            LabeledField("Teléfono") {
                HomeTaskTextField(
                    value = phone.value,
                    onValueChange = { phone.value = it },
                    placeholder = "+57 300 123 4567"
                )
            }

            // Selector Rol
            LabeledField("Rol") {
                ExposedDropdownMenuBox(
                    expanded = expandedRole,
                    onExpandedChange = { expandedRole = !expandedRole }
                ) {
                    // Usamos OutlinedTextField con colores forzados para asegurar visibilidad
                    OutlinedTextField(
                        value = selectedRole.first,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedTextColor = WhiteText,
                            unfocusedTextColor = WhiteText,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false }
                    ) {
                        roles.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.first) },
                                onClick = { selectedRole = item; expandedRole = false }
                            )
                        }
                    }
                }
            }

            // Selector Hogar
            LabeledField("Hogar") {
                ExposedDropdownMenuBox(
                    expanded = expandedHome,
                    onExpandedChange = { expandedHome = !expandedHome }
                ) {
                    OutlinedTextField(
                        value = selectedHome.first,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHome) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedTextColor = WhiteText,
                            unfocusedTextColor = WhiteText,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedHome,
                        onDismissRequest = { expandedHome = false }
                    ) {
                        homes.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.first) },
                                onClick = { selectedHome = item; expandedHome = false }
                            )
                        }
                    }
                }
            }

            LabeledField("Contraseña Temporal") {
                HomeTaskTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    isPassword = true,
                    placeholder = "Mín. 8 caracteres"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Crear
            if (uiState is SignUpUiState.Loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                PrimaryButton(
                    text = "Crear Miembro",
                    onClick = {
                        // El ViewModel validará la longitud de la contraseña
                        viewModel.crearMiembro(
                            nombre = fullName.value,
                            email = email.value,
                            pass = password.value,
                            telefono = phone.value, // <--- Enviamos el String aquí
                            idRol = selectedRole.second,  // <--- Enviamos el Int aquí
                            idHogar = selectedHome.second // <--- Enviamos el Int aquí
                        )
                    }
                )
            }
        }
    }
}