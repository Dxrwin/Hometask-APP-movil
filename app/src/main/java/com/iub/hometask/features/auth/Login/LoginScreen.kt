package com.iub.hometask.features.auth.Login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToAdminPanel: () -> Unit, // Rol 1
    onNavigateToMemberPanel: () -> Unit, // Rol 2
    onNavigateToSignUp: () -> Unit,
    onBackClick: () -> Unit,
    // Inyectamos el ViewModel usando el Factory correcto
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    // 1. Observamos el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // 2. Estado para controlar el Snackbar y los campos de texto
    val snackbarHostState = remember { SnackbarHostState() }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val rememberMe = remember { mutableStateOf(false) }

    // 3. EFECTO: Escuchar errores y mostrar Snackbar
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Error) {
            val mensaje = (uiState as LoginUiState.Error).message
            snackbarHostState.showSnackbar(
                message = mensaje,
                actionLabel = "OK",
                duration = SnackbarDuration.Long
            )
        }
    }

    // 4. EFECTO: Navegación por Rol cuando el login es exitoso
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val roleId = (uiState as LoginUiState.Success).roleId
            if (roleId == 1) {
                onNavigateToAdminPanel()
            } else {
                onNavigateToMemberPanel()
            }
        }
    }

    // 5. ESTRUCTURA VISUAL PRINCIPAL (Scaffold envuelve TODO)
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent // Fondo transparente para respetar tu diseño
    ) { paddingValues ->

        // Usamos Box para gestionar el padding del Scaffold y centrar contenido si es necesario
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // AQUI VA TODO TU CONTENIDO ORIGINAL
            AuthScreenContainer {
                // Header y Botón Atrás
                BackButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.Start)
                )

                AuthHeader(
                    title = "Bienvenido de nuevo",
                    subtitle = "Inicia sesión para gestionar tu hogar"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Formulario (Campos de texto)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    // Agregamos scroll por seguridad en pantallas pequeñas
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    LabeledField(label = "Correo") {
                        HomeTaskTextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            placeholder = "tu@email.com"
                        )
                    }

                    LabeledField(label = "Contraseña") {
                        HomeTaskTextField(
                            value = password.value,
                            onValueChange = { password.value = it },
                            placeholder = "***",
                            isPassword = true
                        )
                    }
                }

                // Opciones Extra (Recordarme / Olvidé contraseña)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe.value,
                        onCheckedChange = { rememberMe.value = it }
                    )
                    Text(
                        text = "Recordarme",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ForgotPasswordText(onClick = { /* TODO: flujo recuperación */ })
                }

                // Botón Principal
                PrimaryButton(
                    text = if (uiState is LoginUiState.Loading) "Cargando..." else "Iniciar Sesión",
                    enabled = uiState !is LoginUiState.Loading,
                    onClick = {
                        viewModel.login(email.value, password.value)
                    }
                )

                // Footer (Tips de seguridad y Registro)
                SecurityTipCard()

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿No tienes una cuenta? ",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Regístrate",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .clickable { onNavigateToSignUp() }
                    )
                }
            }
        }
    }
}