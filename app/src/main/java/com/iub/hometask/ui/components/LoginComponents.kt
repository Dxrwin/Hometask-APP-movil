package com.iub.hometask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun ForgotPasswordText(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: puedes envolver esto con clickable cuando quieras funcionalidad
    Text(
        text = "¿Olvidaste tu contraseña?",
        color = TextSecondary,
        fontSize = 13.sp,
        modifier = modifier
    )
}

@Composable
fun SecurityTipCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "Consejo de seguridad",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "No compartas tu contraseña y evita usar la misma en otros sitios.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
