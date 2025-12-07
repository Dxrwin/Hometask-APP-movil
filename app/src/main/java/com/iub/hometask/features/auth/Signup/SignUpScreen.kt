package com.iub.hometask.features.auth.Signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit, // Navegar al Login (o Panel según flujo)
    onNavigateToLogin: () -> Unit,
    // Inyección del ViewModel con el Factory que creamos
    viewModel: SignUpViewModel = viewModel(factory = SignUpViewModel.Factory)
) {
    // 1. Observamos el estado de la UI (Idle, Loading, Success, Error)
    val uiState by viewModel.uiState.collectAsState()

    // 2. Variables locales para los campos de texto
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val inviteCode = remember { mutableStateOf("") }

    // 3. Reacción a eventos (Navegación al completar registro)
    LaunchedEffect(uiState) {
        if (uiState is SignUpUiState.Success) {
            onSignUpSuccess() // El ViewModel ya validó todo, navegamos
        }
    }

    AuthScreenContainer {
        // --- Header ---
        BackButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start)
        )

        AuthHeader(
            title = "Crea tu cuenta",
            subtitle = "Únete para empezar a gestionar tu hogar",
            showCircleIcon = false
        )

        // --- Formulario ---
        // Añadimos scroll por si el teclado tapa campos en pantallas pequeñas
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            LabeledField("Nombre completo") {
                HomeTaskTextField(
                    value = fullName.value,
                    onValueChange = { fullName.value = it },
                    placeholder = "Tu nombre completo"
                )
            }
            LabeledField("Correo electrónico") {
                HomeTaskTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    placeholder = "tu@email.com"
                )
            }
            LabeledField("Contraseña") {
                HomeTaskTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = "Mínimo 8 caracteres",
                    isPassword = true
                )
            }
            LabeledField("Confirmar contraseña") {
                HomeTaskTextField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    placeholder = "Repite la contraseña",
                    isPassword = true
                )
            }
        }

        // --- Mensaje de Error ---
        if (uiState is SignUpUiState.Error) {
            Text(
                text = (uiState as SignUpUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // --- Botón de Registro con Estado de Carga ---
        if (uiState is SignUpUiState.Loading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            PrimaryButton(
                text = "Registrarse",
                onClick = {
                    // Llamamos a la lógica del ViewModel
                    viewModel.registrarse(
                        nombre = fullName.value,
                        email = email.value,
                        pass = password.value,
                        confirmPass = confirmPassword.value
                    )
                }
            )
        }

        // --- Separador ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f).height(1.dp)
                    .background(TextSecondary.copy(alpha = 0.3f))
            )
            Text(
                text = "  o  ",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Box(
                modifier = Modifier.weight(1f).height(1.dp)
                    .background(TextSecondary.copy(alpha = 0.3f))
            )
        }

        // --- Unirse a Hogar (Funcionalidad futura) ---
        Text(
            text = "Únete a un hogar existente",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HomeTaskTextField(
                value = inviteCode.value,
                onValueChange = { inviteCode.value = it },
                placeholder = "Código de invitación",
                modifier = Modifier.weight(1f)
            )
            PrimaryButton(
                text = "Unirse",
                modifier = Modifier.weight(0.7f),
                onClick = { /* TODO: Implementar unirse a hogar */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Footer: Ir a Login ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¿Ya tienes una cuenta? ",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Text(
                text = "Iniciar sesión",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(start = 2.dp)
                    .clickable { onNavigateToLogin() }
            )
        }
    }
}