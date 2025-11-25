package com.iub.hometask.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            // usamos BackButton dentro del TopBar reutilizable si quieres,
            // pero para mantenerlo simple, solo el título:
            HomeTaskTopBar(
                title = "Mi Perfil"
            )
        },
        bottomBar = {
            BottomNavProfile()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // botón back flotando dentro de la pantalla
            BackButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.Start)
            )

            ProfileHeader(
                name = "Miguel Ángel",
                email = "miguel.angel@email.com",
                onEditProfileClick = { /* TODO */ }
            )

            // Información de contacto
            SectionCard(title = "Información de Contacto") {
                InfoRow(
                    label = "Teléfono",
                    value = "+34 123 456 789"
                )
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(
                    label = "Dirección",
                    value = "Calle Falsa 123, Madrid"
                )
            }

            // Preferencias
            SectionCard(title = "Preferencias") {
                val pushEnabled = remember { mutableStateOf(true) }
                val emailEnabled = remember { mutableStateOf(false) }
                val darkModeEnabled = remember { mutableStateOf(true) }

                PreferenceSwitchRow(
                    label = "Notificaciones Push",
                    checked = pushEnabled.value,
                    onCheckedChange = { pushEnabled.value = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PreferenceSwitchRow(
                    label = "Notificaciones por Email",
                    checked = emailEnabled.value,
                    onCheckedChange = { emailEnabled.value = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PreferenceSwitchRow(
                    label = "Modo Oscuro",
                    checked = darkModeEnabled.value,
                    onCheckedChange = { darkModeEnabled.value = it }
                )
            }

            // Opciones de cuenta (Cambiar contraseña, Idioma)
            SectionCard(title = "") {
                SimpleOptionRow(label = "Cambiar Contraseña")
                SectionDivider()
                SimpleOptionRow(
                    label = "Idioma",
                    trailing = {
                        Text(
                            text = "Español",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            // Eliminar cuenta
            DeleteAccountSection()

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
