package com.iub.hometask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.TextPrimary

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(CardDark)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // TODO: Reemplazar este texto por un Icon de flecha real.
        // Ejemplo cuando tengas Icons:
        // Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
        Text(
            text = "<",
            color = TextPrimary,
            fontSize = 18.sp
        )
    }
}
