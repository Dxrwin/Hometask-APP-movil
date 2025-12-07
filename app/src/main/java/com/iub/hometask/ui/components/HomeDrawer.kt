package com.iub.hometask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.iub.hometask.navigation.Routes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

data class DrawerDestination(
    val route: String,
    val label: String,
    val emoji: String
)

private val drawerItems = listOf(
    DrawerDestination(Routes.PANEL, "Panel del Hogar", "🏠"),
    DrawerDestination(Routes.TASKS, "Tareas", "📋"),
    DrawerDestination(Routes.CALENDAR, "Calendario", "📅"),
    DrawerDestination(Routes.MEMBERS, "Miembros", "👥"),
    // botón de Chat (abrirá el chat con un miembro por defecto, ej: 1)
    DrawerDestination(Routes.chat(1), "Chat", "💬"),
    DrawerDestination(Routes.PROFILE, "Perfil", "👤"),
    DrawerDestination(Routes.SETTINGS, "Ajustes del Hogar", "⚙️")
)

@Composable
fun HomeDrawerContent(
    currentRoute: String,
    onDestinationSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 32.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "HomeTask",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Menú principal",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            drawerItems.forEach { item ->
                DrawerItem(
                    item = item,
                    selected = item.route == currentRoute,
                    onClick = { onDestinationSelected(item.route) }
                )
            }
        }
    }
}

@Composable
private fun DrawerItem(
    item: DrawerDestination,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) CardDark else BackgroundDark

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = item.emoji,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.label,
            color = TextPrimary,
            fontSize = 14.sp
        )
    }
}
