@file:OptIn(ExperimentalMaterial3Api::class)

package com.iub.hometask.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.TextPrimary

@Composable
fun HomeTaskTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            if (navigationIcon != null && onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    // TODO: aquí va el ícono de back que tú quieras
                    // Ejemplo cuando tengas Icons:
                    // Icon(imageVector = navigationIcon, contentDescription = "Volver")
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Navigation icon"
                    )
                }
            }
        },
        actions = {
            if (actionIcon != null && onActionClick != null) {
                IconButton(onClick = onActionClick) {
                    // TODO: aquí va el ícono de acción (compartir, editar, etc.)
                    // Icon(imageVector = actionIcon, contentDescription = "Acción")
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = "Action icon"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundDark
        )
    )
}
