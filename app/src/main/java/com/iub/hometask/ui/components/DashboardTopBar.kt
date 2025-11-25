package com.iub.hometask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // botón de menú (tres rayas)
        Text(
            text = "☰",
            color = TextPrimary,
            fontSize = 20.sp,
            modifier = Modifier
                .clickable { onMenuClick() }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Panel del Hogar",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        // icono perfil
        Text(
            text = "👤",
            color = TextPrimary,
            fontSize = 20.sp,
            modifier = Modifier.clickable { onProfileClick() }
        )

        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Panel del Hogar",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hola, bienvenido de nuevo",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = TextPrimary
                    )
                }
            },
            actions = {
                // Botón Mensajería (Chat)
                IconButton(
                    onClick = { /* TODO: Navegar a mensajes generales */ },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline, // Necesitas un icono de chat (burbuja)
                        contentDescription = "Mensajes",
                        tint = TextPrimary
                    )
                }

                // Botón Configuración
                IconButton(
                    onClick = { /* TODO: Navegar a configuración */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración",
                        tint = TextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent // Transparente para ver el fondo oscuro
            )
        )
    }
}
