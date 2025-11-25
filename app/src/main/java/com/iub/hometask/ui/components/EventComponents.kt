package com.iub.hometask.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.data.mock.Member
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun EventInfoCard(
    title: String,
    description: String,
    dateLabel: String,
    modifier: Modifier = Modifier // Importante para Hero Transition
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Fila de Fecha con Icono
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(TextSecondary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📅", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = dateLabel,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ParticipantsPile(
    participants: List<Member>,
    onAddClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((-12).dp) // Efecto de apilado
    ) {
        participants.take(4).forEach { member ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, CardDark, CircleShape) // Borde del color de fondo para separar
                    .background(Color.Gray, CircleShape), // Aquí iría Coil Image
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Botón "+"
        Box(
            modifier = Modifier
                .padding(start = 12.dp)
                .size(48.dp)
                .border(1.dp, TextSecondary.copy(alpha = 0.5f), CircleShape)
                .background(Color.Transparent, CircleShape)
            //.clickable { onAddClick() }
            ,
            contentAlignment = Alignment.Center
        ) {
            Text("+", color = TextSecondary, fontSize = 24.sp)
        }
    }
}