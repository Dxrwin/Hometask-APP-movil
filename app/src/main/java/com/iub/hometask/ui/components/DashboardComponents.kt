package com.iub.hometask.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.* // Asegúrate de tener tus colores importados

// --- 1. SECCIÓN PROGRESO DEL EQUIPO ---
@Composable
fun TeamProgressSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text = "Progreso del Equipo",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TeamMemberItem(name = "Juan", progress = 0.7f, color = PrimaryBlue)
            TeamMemberItem(name = "Sara", progress = 0.4f, color = Color(0xFFE91E63)) // Rosa
            TeamMemberItem(name = "Tú", progress = 0.9f, color = Color(0xFF8BC34A))   // Verde
        }
    }
}



@Composable
fun TeamMemberItem(name: String, progress: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
            CircularProgressIndicator(
                progress = { progress }, modifier = Modifier.fillMaxSize(),
                color = color, strokeWidth = 4.dp, trackColor = CardDark
            )
            Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

// --- 2. TARJETAS (Igual que antes) ---
@Composable
fun DashboardStatsGrid(onTasksClick: () -> Unit, onMembersClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("12", "Tareas", "✓", Color(0xFF4CAF50), Modifier.weight(1f), onTasksClick)
        StatCard("4", "Miembros", "👥", Color(0xFF9C27B0), Modifier.weight(1f), onMembersClick)
    }
}

@Composable
fun StatCard(title: String, subtitle: String, icon: String, iconColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.height(110.dp).clip(RoundedCornerShape(24.dp)).background(CardDark).clickable { onClick() },
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 24.sp, color = iconColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(subtitle, fontSize = 12.sp, color = TextSecondary)
    }
}

// --- 3. CRONOLOGÍA DE EVENTOS (ANIMADA Y CLICKEABLE) ---
@Composable
fun EventTimelineSection(onEventClick: (Int) -> Unit) {
    // Animación de trazado
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(500)
        animatedProgress.animateTo(1f, animationSpec = tween(1500, easing = LinearEasing))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(CardDark, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text("Cronología de Eventos", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        TimelineItem("Reunión familiar", "Ahora", "Planificación de vacaciones", PrimaryBlue, false, animatedProgress.value, { onEventClick(100) })
        TimelineItem("Pago de factura de luz", "En 2 horas", "Vence hoy a las 23:00", Color(0xFFFFC107), false, animatedProgress.value, { onEventClick(101) })
        TimelineItem("Limpieza general", "Mañana", "Toda la casa, 10:00 AM", TextSecondary, true, animatedProgress.value, { onEventClick(102) })
    }
}

@Composable
fun TimelineItem(
    title: String, time: String, subtitle: String, dotColor: Color, isLast: Boolean,
    animProgress: Float, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // REDIRECCIÓN AL CLICK
            .height(IntrinsicSize.Min)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            Box(modifier = Modifier.size(10.dp).background(dotColor, CircleShape))
            if (!isLast) {
                Canvas(modifier = Modifier.width(2.dp).weight(1f).padding(vertical = 4.dp)) {
                    val lineHeight = size.height * animProgress // ANIMACIÓN DE ALTURA
                    drawLine(
                        color = TextSecondary.copy(alpha = 0.3f),
                        start = Offset(0f, 0f),
                        end = Offset(0f, lineHeight),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 24.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(time, color = TextSecondary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
        }
    }
}

// --- 4. MINI CALENDARIO (DINÁMICO E INTERACTIVO) ---
@Composable
fun DashboardMiniCalendar(
    onCalendarClick: () -> Unit
) {
    val today = remember { LocalDate.now() }
    val days = remember { (0..6).map { today.plusDays(it.toLong()) } }
    val dayFormatter = DateTimeFormatter.ofPattern("d")
    val weekDayFormatter = DateTimeFormatter.ofPattern("EEEEE", Locale("es", "ES")) // L, M, X...

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onCalendarClick() } // CLICK EN TÍTULO VA A TAREAS
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Calendario de Tareas", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver todo >", color = PrimaryBlue, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { date ->
                val isSelected = date == today
                // Simulación de actividad (días pares tienen punto)
                val hasActivity = date.dayOfMonth % 2 == 0

                MiniCalendarDayItem(
                    dayNumber = date.format(dayFormatter),
                    dayLabel = date.format(weekDayFormatter).uppercase(),
                    isSelected = isSelected,
                    hasActivity = hasActivity,
                    onClick = onCalendarClick // CUALQUIER DÍA VA A TAREAS
                )
            }
        }
    }
}

@Composable
fun MiniCalendarDayItem(
    dayNumber: String,
    dayLabel: String,
    isSelected: Boolean,
    hasActivity: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp)) // Forma más alargada como en la imagen
            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.3f) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Text(text = dayLabel, color = TextSecondary, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dayNumber,
            color = if (isSelected) PrimaryBlue else TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Punto indicador de tareas
        if (hasActivity) {
            Box(modifier = Modifier.size(8.dp).background(Color.Gray, CircleShape))
        } else {
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

// --- 5. FAB (ROBOT CON PULSO) ---
@Composable
fun PrimaryFab(onClick: () -> Unit) {
    // Animación de pulso
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Restart),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Restart),
        label = "alpha"
    )

    Box(contentAlignment = Alignment.Center) {
        // Círculo de onda detrás
        Box(
            modifier = Modifier
                .size(56.dp)
                .scale(scale)
                .background(PrimaryBlue.copy(alpha = alpha), CircleShape)
        )

        // Botón real
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp).shadow(8.dp, CircleShape),
            containerColor = PrimaryBlue,
            contentColor = TextPrimary
        ) {
            Text(text = "🤖", fontSize = 24.sp)
        }
    }



    @Composable
    fun TeamMemberItem(name: String, progress: Float, color: Color) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = color,
                    strokeWidth = 4.dp,
                    trackColor = CardDark
                )
                // Avatar Placeholder
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }

    // --- 2. TARJETAS DE RESUMEN (GRID) ---
    @Composable
    fun DashboardStatsGrid(
        onTasksClick: () -> Unit,
        onMembersClick: () -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta Tareas
            StatCard(
                title = "12",
                subtitle = "Tareas",
                icon = "✓",
                iconColor = Color(0xFF4CAF50), // Verde
                modifier = Modifier.weight(1f),
                onClick = onTasksClick
            )

            // Tarjeta Miembros
            StatCard(
                title = "4",
                subtitle = "Miembros",
                icon = "👥",
                iconColor = Color(0xFF9C27B0), // Morado
                modifier = Modifier.weight(1f),
                onClick = onMembersClick
            )
        }
    }

    @Composable
    fun TimelineItem(
        title: String,
        time: String,
        subtitle: String,
        dotColor: Color,
        isLast: Boolean,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .height(IntrinsicSize.Min) // Importante para la línea vertical
        ) {
            // Columna de la línea y el punto
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(24.dp)
            ) {
                // El Punto
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(dotColor, CircleShape)
                )
                // La línea punteada
                if (!isLast) {
                    Canvas(modifier = Modifier.width(2.dp).weight(1f).padding(vertical = 4.dp)) {
                        drawLine(
                            color = TextSecondary.copy(alpha = 0.3f),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            @Composable
            fun StatCard(
                title: String,
                subtitle: String,
                icon: String,
                iconColor: Color,
                modifier: Modifier,
                onClick: () -> Unit
            ) {
                Column(
                    modifier = modifier
                        .height(110.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(CardDark)
                        .clickable { onClick() },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = icon, fontSize = 24.sp, color = iconColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
                }
            }

            // --- 3. CRONOLOGÍA DE EVENTOS (TIMELINE) ---
            @Composable
            fun EventTimelineSection(
                onEventClick: (Int) -> Unit // ID del evento
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Cronología de Eventos",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Items de la timeline
                    TimelineItem(
                        title = "Reunión familiar",
                        time = "Ahora",
                        subtitle = "Planificación de vacaciones",
                        dotColor = PrimaryBlue,
                        isLast = false,
                        onClick = { onEventClick(100) }
                    )
                    TimelineItem(
                        title = "Pago de factura de luz",
                        time = "En 2 horas",
                        subtitle = "Vence hoy a las 23:00",
                        dotColor = Color(0xFFFFC107), // Amarillo
                        isLast = false,
                        onClick = { onEventClick(101) }
                    )
                    TimelineItem(
                        title = "Limpieza general",
                        time = "Mañana",
                        subtitle = "Toda la casa, 10:00 AM",
                        dotColor = TextSecondary,
                        isLast = true,
                        onClick = { onEventClick(102) }
                    )
                }
            }

            @Composable
            fun MiniCalendarDay(day: String, label: String, isSelected: Boolean) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PrimaryBlue.copy(alpha = 0.3f) else Color.Transparent)
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                ) {
                    Text(text = label, color = TextSecondary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = day,
                        color = if (isSelected) PrimaryBlue else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Avatar mini si hay tarea
                    if (isSelected || day == "16" || day == "20") {
                        Box(modifier = Modifier.size(16.dp).background(Color.Gray, CircleShape))
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            /*@Composable
fun TimelineItem(
    title: String,
    time: String,
    subtitle: String,
    dotColor: Color,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(IntrinsicSize.Min) // Importante para la línea vertical
    ) {
        // Columna de la línea y el punto
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            // El Punto
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(dotColor, CircleShape)
            )
            // La línea punteada
            if (!isLast) {
                Canvas(modifier = Modifier.width(2.dp).weight(1f).padding(vertical = 4.dp)) {
                    drawLine(
                        color = TextSecondary.copy(alpha = 0.3f),
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Contenido Texto
        Column(modifier = Modifier.padding(bottom = if(isLast) 0.dp else 24.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = time, color = TextSecondary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = TextSecondary, fontSize = 12.sp)
        }
    }
}*/

            // --- 4. MINI CALENDARIO ---
            @Composable
            fun DashboardMiniCalendar() {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(
                        "Calendario de Tareas",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Dias falsos para demo visual
                        MiniCalendarDay("15", "L", false)
                        MiniCalendarDay("16", "M", false)
                        MiniCalendarDay("17", "X", false)
                        MiniCalendarDay("18", "J", true) // Seleccionado
                        MiniCalendarDay("19", "V", false)
                        MiniCalendarDay("20", "S", false)
                        MiniCalendarDay("21", "D", false)
                    }
                }
            }
        }
    }
}

/*@Composable
fun MiniCalendarDay(day: String, label: String, isSelected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.3f) else Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Text(text = label, color = TextSecondary, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = day,
            color = if (isSelected) PrimaryBlue else TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Avatar mini si hay tarea
        if (isSelected || day == "16" || day == "20") {
            Box(modifier = Modifier.size(16.dp).background(Color.Gray, CircleShape))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- FAB (Robot) ---
@Composable
fun PrimaryFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp).shadow(8.dp, CircleShape),
        containerColor = PrimaryBlue,
        contentColor = TextPrimary
    ) {
        Text(text = "🤖", fontSize = 24.sp)
    }
}*/







/*package com.iub.hometask.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun AccordionCard(
    title: String,
    modifier: Modifier = Modifier,
    rightActionText: String? = null,
    initiallyExpanded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            if (rightActionText != null) {
                Text(
                    text = rightActionText,
                    color = PrimaryBlue,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (expanded) "▲" else "▼",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
        }
    }
}

@Composable
fun UpcomingTaskItem(
    title: String,
    assignedTo: String,
    dueLabel: String,
    time: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = dueLabel,
                color = PrimaryBlue,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Asignado a: $assignedTo",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = time,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun RecentActivityItem(
    description: String,
    timeLabel: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(CardDark, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // TODO: icono de check / acción
            Text(text = "✓", color = TextPrimary, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = description,
                color = TextPrimary,
                fontSize = 13.sp
            )
            Text(
                text = timeLabel,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun MembersSummarySection(
    onManageClick: () -> Unit,
    onAddTaskClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Miembros",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón Gestionar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardDark)
                    .clickable { onManageClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Gestionar",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Botón Añadir Tarea
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardDark)
                    .clickable { onAddTaskClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "↻", // TODO: icono circular como en la maqueta
                            color = PrimaryBlue,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Añadir Tarea",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
@Composable
fun PrimaryFab(
    onClick: () -> Unit
) {
    // 1. Configuración de la animación de "Respiración" (Pulse)
    val infiniteTransition = rememberInfiniteTransition(label = "robot_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // 2. Lógica del Tooltip (Aparece a los 2s, se va a los 7s)
    var showTooltip by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000) // Espera inicial
        showTooltip = true
        delay(5000) // Tiempo visible
        showTooltip = false
    }

    Box(contentAlignment = Alignment.BottomEnd) {

        // El Tooltip flotante
        AnimatedVisibility(
            visible = showTooltip,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.padding(end = 70.dp, bottom = 10.dp) // Posición a la izq del botón
        ) {
            Box(
                modifier = Modifier
                    .background(CardDark, RoundedCornerShape(12.dp))
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "¿Necesitas ayuda?",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                // Triangulito decorativo (opcional, simplificado aquí)
            }
        }

        // El Botón con efecto Pulse
        Box(contentAlignment = Alignment.Center) {
            // Círculo de la onda expansiva
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(pulseScale)
                    .background(PrimaryBlue.copy(alpha = pulseAlpha), CircleShape)
            )

            // El botón real
            FloatingActionButton(
                onClick = onClick,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape),
                containerColor = PrimaryBlue,
                contentColor = TextPrimary
            ) {
                Text(text = "🤖", fontSize = 24.sp)
            }
        }
    }
}

/*@Composable
fun PrimaryFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .shadow(8.dp, CircleShape),
        containerColor = PrimaryBlue,
        contentColor = TextPrimary
    ) {
        // Opción 1: usar emoji de robot (rápido y sin drawables)
        Text(
            text = "🤖",
            fontSize = 22.sp
        )

        // Opción 2 (recomendada): usar un vector asset de robot
        // 1. Crea un Vector Asset en res/drawable con nombre ic_robot_assistant
        // 2. Reemplaza el Text de arriba por este Icon:

        /*
        Icon(
            painter = painterResource(id = R.drawable.ic_robot_assistant),
            contentDescription = "Asistente",
            modifier = Modifier.size(28.dp)
        )
        */
        // IMPORTANTE: si usas la opción 2, añade los imports:
        // import androidx.compose.material3.Icon
        // import androidx.compose.ui.res.painterResource
    }
}*/


// ---------------------------------------------------------
//  SECCIÓN "RESUMEN" + PRÓXIMAS TAREAS
// ---------------------------------------------------------

@Composable
fun DashboardSummarySection(
    onTaskClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Resumen",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        AccordionCard(
            title = "Próximas Tareas",
            rightActionText = "Ver Todas"
        ) {
            UpcomingTaskItem(
                title = "Limpiar la cocina",
                assignedTo = "Juan",
                dueLabel = "Vence Hoy",
                time = "22:00",
                onClick = onTaskClick
            )
            UpcomingTaskItem(
                title = "Sacar la basura",
                assignedTo = "Sara",
                dueLabel = "Vence Mañana",
                time = "20:00",
                onClick = onTaskClick
            )
        }
    }
}

// ---------------------------------------------------------
//  SECCIÓN "ACTIVIDAD RECIENTE"
// ---------------------------------------------------------

@Composable
fun DashboardRecentActivitySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        AccordionCard(
            title = "Actividad Reciente"
        ) {
            RecentActivityItem(
                description = "Juan completó \"Limpiar la cocina\".",
                timeLabel = "Hace 2 horas"
            )
            RecentActivityItem(
                description = "Tú añadiste \"Comprar leche\".",
                timeLabel = "Ayer"
            )
        }
    }
}*/

