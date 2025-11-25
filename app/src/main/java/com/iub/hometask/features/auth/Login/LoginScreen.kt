package com.iub.hometask.features.auth.Login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onBackClick: () -> Unit
) {
    AuthScreenContainer {
        // back arrow
        BackButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start)
        )

        AuthHeader(
            title = "Bienvenido de nuevo",
            subtitle = "Inicia sesión para gestionar tu hogar"
        )

        Spacer(modifier = Modifier.height(8.dp))

        val email = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val rememberMe = remember { mutableStateOf(false) }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LabeledField(label = "Nombre de usuario o Correo") {
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
                    placeholder = "Contraseña",
                    isPassword = true
                )
            }
        }

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
            // componente extra 1
            ForgotPasswordText(onClick = { /* TODO: flujo recuperación */ })
        }

        PrimaryButton(
            text = "Iniciar Sesión",
            onClick = { onLoginSuccess() }
        )

        // componente extra 2
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
