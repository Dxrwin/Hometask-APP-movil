package com.iub.hometask.features.auth.Signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    AuthScreenContainer {
        BackButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start)
        )

        AuthHeader(
            title = "Crea tu cuenta",
            subtitle = "Únete para empezar a gestionar tu hogar",
            showCircleIcon = false
        )

        val fullName = remember { mutableStateOf("") }
        val email = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val confirmPassword = remember { mutableStateOf("") }
        val inviteCode = remember { mutableStateOf("") }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    placeholder = "Contraseña",
                    isPassword = true
                )
            }
            LabeledField("Confirmar contraseña") {
                HomeTaskTextField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    placeholder = "Confirmar contraseña",
                    isPassword = true
                )
            }
        }

        PrimaryButton(
            text = "Registrarse",
            onClick = { onSignUpSuccess() }
        )

        // separador simple:
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(TextSecondary.copy(alpha = 0.3f))
            )
            Text(
                text = "  o  ",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(TextSecondary.copy(alpha = 0.3f))
            )
        }

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
                onClick = { /* TODO: usar código */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
