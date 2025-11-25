package com.iub.hometask.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun ActionPlanTimeline(
    steps: List<ActionStep>
) {
    // Animación de la línea vertical (0f a 1f)
    val lineProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(300) // Pequeño delay para que cargue la pantalla
        lineProgress.animateTo(1f, animationSpec = tween(1000))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Header con botón Asistente
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Plan de acción",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // Botón Asistente con efecto
            AssistantSmallButton()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // La Timeline
        Box(modifier = Modifier.fillMaxWidth()) {
            // Dibujamos la línea de fondo conectora (Animada)
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .padding(start = 11.dp, top = 10.dp, bottom = 30.dp) // Ajuste fino para alinear con puntos
            ) {
                val height = size.height
                drawLine(
                    color = TextSecondary.copy(alpha = 0.3f),
                    start = Offset(0f, 0f),
                    end = Offset(0f, height * lineProgress.value), // Se dibuja hacia abajo
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                steps.forEachIndexed { index, step ->
                    TimelineItem(step = step, showDelay = index * 200)
                }
            }
        }
    }
}

@Composable
fun TimelineItem(step: ActionStep, showDelay: Int) {
    // Animación de aparición escalonada
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(showDelay.toLong())
        alpha.animateTo(1f)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // El punto (Dot)
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(24.dp)
                .background(CardDark, CircleShape) // Borde falso para separar de la línea
                .border(
                    width = 2.dp,
                    color = if (step.isCurrent) PrimaryBlue else TextSecondary.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .padding(4.dp) // Espacio interior
        ) {
            if (step.isCurrent) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryBlue, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Contenido
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = step.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                // Icono de bookmark (ejemplo)
                Text(text = "🔖", fontSize = 12.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chips de estado
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(text = "Realizando", isSelected = step.status == StepStatus.IN_PROGRESS)
                StatusChip(text = "Terminado", isSelected = step.status == StepStatus.DONE)
            }
        }
    }
}

@Composable
fun StatusChip(text: String, isSelected: Boolean) {
    val bgColor = if (isSelected) PrimaryBlue else Color.Transparent
    val borderColor = if (isSelected) PrimaryBlue else TextSecondary.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun AssistantSmallButton() {
    // Aquí iría el efecto Shimmer
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryBlue.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "✨", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Asistente", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// Modelo de datos simple para la UI
data class ActionStep(
    val title: String,
    val isCurrent: Boolean,
    val status: StepStatus
)

enum class StepStatus { PENDING, IN_PROGRESS, DONE }