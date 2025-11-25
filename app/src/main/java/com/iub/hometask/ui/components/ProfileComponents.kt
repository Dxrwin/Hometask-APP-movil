package com.iub.hometask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.ErrorRed
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun ProfileHeader(
    name: String,
    email: String,
    onEditProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Aquí va la imagen del usuario (Image con painterResource / AsyncImage)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Text(
            text = email,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Editar Perfil",
            modifier = Modifier.fillMaxWidth(0.7f),
            onClick = onEditProfileClick
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .padding(16.dp)
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        content()
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp
        )
    }
}

@Composable
fun PreferenceSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SimpleOptionRow(
    label: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 14.sp
        )
        if (trailing != null) {
            Spacer(modifier = Modifier.weight(1f))
            trailing()
        }
    }
}

@Composable
fun DeleteAccountSection() {
    SectionCard(title = "") {
        Text(
            text = "Eliminar Cuenta",
            color = ErrorRed,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SectionDivider() {
    Divider(color = TextSecondary.copy(alpha = 0.2f))
}

@Composable
fun BottomNavProfile() {
    NavigationBar(
        containerColor = BackgroundDark
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Panel */ },
            icon = {
                // TODO: icono Panel
            },
            label = { Text(text = "Panel", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Tareas */ },
            icon = {
                // TODO: icono Tareas
            },
            label = { Text(text = "Tareas", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Calendario */ },
            icon = {
                // TODO: icono Calendario
            },
            label = { Text(text = "Calendario", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = true,
            onClick = { /* ya estás en Perfil */ },
            icon = {
                // TODO: icono Perfil
            },
            label = { Text(text = "Perfil", fontSize = 11.sp) }
        )
    }
}
