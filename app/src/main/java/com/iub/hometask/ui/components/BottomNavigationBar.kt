package com.iub.hometask.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.theme.BackgroundDark

@Composable
fun HomeBottomNavigationBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = BackgroundDark
    ) {
        NavigationBarItem(
            selected = currentRoute == Routes.PANEL,
            onClick = { onItemSelected(Routes.PANEL) },
            icon = {
                // TODO: reemplazar por Icon real si quieres
                Text(text = "🏠", fontSize = 16.sp)
            },
            label = { Text(text = "Panel", fontSize = 11.sp) }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.TASKS,
            onClick = { onItemSelected(Routes.TASKS) },
            icon = {
                Text(text = "📋", fontSize = 16.sp)
            },
            label = { Text(text = "Tareas", fontSize = 11.sp) }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.CALENDAR,
            onClick = { onItemSelected(Routes.CALENDAR) },
            icon = {
                Text(text = "📅", fontSize = 16.sp)
            },
            label = { Text(text = "Calendario", fontSize = 11.sp) }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.MEMBERS,
            onClick = { onItemSelected(Routes.MEMBERS) },
            icon = {
                Text(text = "👥", fontSize = 16.sp)
            },
            label = { Text(text = "Miembros", fontSize = 11.sp) }
        )
    }
}
