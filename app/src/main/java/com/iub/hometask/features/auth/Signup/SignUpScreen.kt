package com.iub.hometask.features.auth.Signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SignUpViewModel = viewModel(factory = SignUpViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Variables
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") } // <--- NUEVO
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    // Selectores: Pair("Texto a mostrar", Valor_Int_Para_DTO)
    val roles = listOf("Administrador" to 1, "Miembro" to 2)
    var selectedRole by remember { mutableStateOf(roles[1]) }
    var expandedRole by remember { mutableStateOf(false) }

    val homes = listOf("Hogar Principal (1)" to 1, "Hogar Secundario (2)" to 2)
    var selectedHome by remember { mutableStateOf(homes[0]) }
    var expandedHome by remember { mutableStateOf(false) }

    // Efectos (Snackbar y Navegación)
    LaunchedEffect(uiState) {
        when (uiState) {
            is SignUpUiState.Error -> snackbarHostState.showSnackbar((uiState as SignUpUiState.Error).message)
            is SignUpUiState.Success -> {
                snackbarHostState.showSnackbar("Cuenta creada con éxito")
                onSignUpSuccess()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AuthScreenContainer {
                BackButton(onClick = onBackClick, modifier = Modifier.align(Alignment.Start))
                AuthHeader(title = "Crea tu cuenta", subtitle = "Únete y configura tu perfil")

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    LabeledField("Nombre completo") {
                        HomeTaskTextField(value = fullName.value, onValueChange = { fullName.value = it }, placeholder = "Tu nombre")
                    }

                    LabeledField("Correo electrónico") {
                        HomeTaskTextField(value = email.value, onValueChange = { email.value = it }, placeholder = "ejemplo@correo.com")
                    }

                    // --- NUEVO CAMPO: TELÉFONO ---
                    LabeledField("Teléfono") {
                        HomeTaskTextField(
                            value = phone.value,
                            onValueChange = { phone.value = it },
                            placeholder = "+57 300...",
                            // Opcional: Configurar teclado numérico si quieres
                            // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }

                    // --- SELECTOR ROL (Devuelve Int) ---
                    LabeledField("Tu Rol") {
                        ExposedDropdownMenuBox(
                            expanded = expandedRole,
                            onExpandedChange = { expandedRole = !expandedRole }
                        ) {
                            OutlinedTextField(
                                value = selectedRole.first, // Muestra Texto
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = com.iub.hometask.ui.theme.InputBackground,
                                    unfocusedContainerColor = com.iub.hometask.ui.theme.InputBackground
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedRole,
                                onDismissRequest = { expandedRole = false }
                            ) {
                                roles.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item.first) },
                                        onClick = {
                                            selectedRole = item // Guarda el Pair(Texto, Int)
                                            expandedRole = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // --- SELECTOR HOGAR (Devuelve Int) ---
                    LabeledField("Selecciona Hogar") {
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
                                    focusedContainerColor = com.iub.hometask.ui.theme.InputBackground,
                                    unfocusedContainerColor = com.iub.hometask.ui.theme.InputBackground
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedHome,
                                onDismissRequest = { expandedHome = false }
                            ) {
                                homes.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item.first) },
                                        onClick = {
                                            selectedHome = item // Guarda el Pair(Texto, Int)
                                            expandedHome = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    LabeledField("Contraseña") {
                        HomeTaskTextField(value = password.value, onValueChange = { password.value = it }, placeholder = "Mínimo 8 caracteres", isPassword = true)
                    }

                    LabeledField("Confirmar contraseña") {
                        HomeTaskTextField(value = confirmPassword.value, onValueChange = { confirmPassword.value = it }, placeholder = "Repite la contraseña", isPassword = true)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN REGISTRAR
                if (uiState is SignUpUiState.Loading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    PrimaryButton(
                        text = "Registrarse",
                        onClick = {
                            viewModel.registrarse(
                                nombre = fullName.value,
                                email = email.value,
                                pass = password.value,
                                confirmPass = confirmPassword.value,
                                telefono = phone.value, // <--- Enviamos el teléfono
                                idRol = selectedRole.second,  // <--- Enviamos el Int (1 o 2)
                                idHogar = selectedHome.second // <--- Enviamos el Int (1 o 2)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("¿Ya tienes cuenta? ", color = TextSecondary, fontSize = 13.sp)
                    Text("Iniciar sesión", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, modifier = Modifier.clickable { onNavigateToLogin() })
                }
            }
        }
    }
}