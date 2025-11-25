package com.iub.hometask.ui.components


import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Dialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.iub.hometask.data.mock.Member
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ---------------------------------------------------------
//  MODELO DE EVENTO PARA EL CALENDARIO
// ---------------------------------------------------------

data class CalendarUiEvent(
    val id: Int,
    val title: String,
    val description: String,
    val timeLabel: String,           // "10:00", "18:00"
    val placeLabel: String,          // "Toda la casa", "Parque central"
    val participants: List<Member>,
    val color: CalendarEventColor,
    val iconEmoji: String? = null    // Para los iconos del listado semanal / mensual
)

enum class CalendarEventColor {
    YELLOW,
    PURPLE,
    GREEN
}

// 1. EFECTO ANIMATE ENTER (Lo tenías perdido)
fun Modifier.animateEnter(index: Int): Modifier = composed {
    val alphaAnim = remember { Animatable(0f) }
    val transYAnim = remember { Animatable(50f) }

    LaunchedEffect(Unit) {
        delay(index * 50L)
        alphaAnim.animateTo(1f, animationSpec = tween(300))
        transYAnim.animateTo(0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    this.graphicsLayer {
        alpha = alphaAnim.value
        translationY = transYAnim.value
    }
}


// 2. EL COMPONENTE DE LA LISTA (Definición correcta)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DayEventsList(
    date: LocalDate,
    events: List<CalendarUiEvent>,
    onEventClick: (CalendarUiEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    val dateText = date.format(formatter).replaceFirstChar { it.uppercase() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = dateText, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "${events.size} tareas/eventos", color = TextSecondary, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(events) { index, event ->
                val heroKey = if (event.id < 100) "task-${event.id}" else "event-${event.id}"

                Box(modifier = Modifier.animateEnter(index)) {
                    CalendarEventCard(
                        event = event,
                        onClick = { onEventClick(event) },
                        modifier = Modifier.sharedElement(
                            // Pasa los argumentos SIN NOMBRE para evitar errores
                            rememberSharedContentState(key = heroKey),
                            animatedVisibilityScope
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

// ---------------------------------------------------------
//  TOP BAR "Junio 2024   🔍   ☰"
// ---------------------------------------------------------

@Composable
fun CalendarTopBar(
    title: String,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = " ▾",
            color = TextSecondary,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Búsqueda
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardDark)
                .clickable { onSearchClick() },
            contentAlignment = Alignment.Center
        ) {
            // TODO: reemplazar por icono vectorial de búsqueda
            Text(text = "🔍", color = TextPrimary, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Filtro
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardDark)
                .clickable { onFilterClick() },
            contentAlignment = Alignment.Center
        ) {
            // TODO: reemplazar por icono vectorial de filtros
            Text(text = "☰", color = TextPrimary, fontSize = 18.sp)
        }
    }
}

// ---------------------------------------------------------
//  TOGGLE Día / Semana / Mes
// ---------------------------------------------------------

enum class CalendarMode {
    DAY, WEEK, MONTH
}

@Composable
fun CalendarModeToggle(
    mode: CalendarMode,
    onModeChange: (CalendarMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(CardDark, RoundedCornerShape(24.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CalendarModeChip(
            text = "Día",
            selected = mode == CalendarMode.DAY,
            onClick = { onModeChange(CalendarMode.DAY) },
            modifier = Modifier.weight(1f)
        )
        CalendarModeChip(
            text = "Semana",
            selected = mode == CalendarMode.WEEK,
            onClick = { onModeChange(CalendarMode.WEEK) },
            modifier = Modifier.weight(1f)
        )
        CalendarModeChip(
            text = "Mes",
            selected = mode == CalendarMode.MONTH,
            onClick = { onModeChange(CalendarMode.MONTH) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CalendarModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) PrimaryBlue else Color.Transparent
    val textColor = if (selected) TextPrimary else TextSecondary

    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ---------------------------------------------------------
//  VISTA SEMANAL (lista vertical de días)
// ---------------------------------------------------------

@Composable
fun CalendarWeekView(
    weekStart: LocalDate,
    eventsByDate: Map<LocalDate, List<CalendarUiEvent>>,
    onEventClick: (CalendarUiEvent) -> Unit
) {
    val days = (0..6).map { weekStart.plusDays(it.toLong()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = days,
            key = { it.toEpochDay() }
        ) { date ->
            val events = eventsByDate[date].orEmpty()
            CalendarWeekDaySection(
                date = date,
                events = events,
                onEventClick = onEventClick
            )
        }
    }
}

@Composable
private fun CalendarWeekDaySection(
    date: LocalDate,
    events: List<CalendarUiEvent>,
    onEventClick: (CalendarUiEvent) -> Unit
) {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
        .uppercase()
    val dayNumber = date.dayOfMonth.toString()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = dayName,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dayNumber,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            if (events.isEmpty()) {
                Text(
                    text = "No hay eventos programados",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        events.forEach { event ->
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                Spacer(modifier = Modifier.width(60.dp))
                CalendarWeekEventCard(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
private fun CalendarWeekEventCard(
    event: CalendarUiEvent,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when (event.color) {
                        CalendarEventColor.YELLOW -> Color(0xFF4D3210)
                        CalendarEventColor.PURPLE -> Color(0xFF30244D)
                        CalendarEventColor.GREEN -> Color(0xFF11402A)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            // TODO: reemplazar por icono vectorial
            Text(
                text = event.iconEmoji ?: "✓",
                color = TextPrimary,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = event.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = event.placeLabel,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ---------------------------------------------------------
//  VISTA DIARIA (línea de tiempo)
// ---------------------------------------------------------

@Composable
fun CalendarDayView(
    date: LocalDate,
    events: List<CalendarUiEvent>,
    onEventClick: (CalendarUiEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier.Companion,
    function: @Composable () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    val dateLabel = date.format(formatter).replaceFirstChar { it.uppercase() }

    val totalEvents = events.size
    val timeSlots = listOf(
        "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00",
        "17:00", "18:00"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dateLabel,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$totalEvents tareas, $totalEvents evento" +
                        if (totalEvents == 1) "" else "s",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(timeSlots.size) { index ->
            val time = timeSlots[index]
            val eventsAtTime = events.filter { it.timeLabel.startsWith(time.substring(0, 2)) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = time,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.width(52.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    eventsAtTime.forEach { event ->
                        CalendarEventCard(
                            event = event,
                            modifier = Modifier.padding(vertical = 4.dp),
                            onClick = { onEventClick(event) }
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------
//  VISTA MENSUAL (grid + tarjetas resumen + lista del día)
// ---------------------------------------------------------

@Composable
fun CalendarMonthView(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    eventsByDate: Map<LocalDate, List<CalendarUiEvent>>,
    onDateSelected: (LocalDate) -> Unit,
    onEventClick: (CalendarUiEvent) -> Unit
) {
    val eventsForSelected = eventsByDate[selectedDate].orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 80.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        CalendarMonthGrid(
            currentDate = currentDate,
            selectedDate = selectedDate,
            eventsByDate = eventsByDate,
            onDateSelected = onDateSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        MonthSummaryCard(
            title = "Tareas Prioritarias",
            subtitle = "3 tareas urgentes esta semana",
            emoji = "❗"
        )
        Spacer(modifier = Modifier.height(8.dp))
        MonthSummaryCard(
            title = "Próximas para el Evento",
            subtitle = "5 tareas para \"Comida familiar\"",
            emoji = "📅"
        )

        Spacer(modifier = Modifier.height(16.dp))

        val formatter =
            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
        Text(
            text = selectedDate.format(formatter).replaceFirstChar { it.uppercase() },
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "${eventsForSelected.size} tareas, ${eventsForSelected.size} evento" +
                    if (eventsForSelected.size == 1) "" else "s",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(eventsForSelected) { event ->
                CalendarEventCard(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    eventsByDate: Map<LocalDate, List<CalendarUiEvent>>,
    onDateSelected: (LocalDate) -> Unit
) {
    val yearMonth = YearMonth.from(currentDate)
    val cells = remember(currentDate) { buildMonthCells(currentDate) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM").forEach { label ->
                Text(
                    text = label,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        cells.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { date ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val hasEvents = eventsByDate[date].orEmpty().isNotEmpty()
                            MonthDayItem(
                                date = date,
                                isSelected = date == selectedDate,
                                hasEvents = hasEvents,
                                isInMonth = date.month == yearMonth.month,
                                onClick = { onDateSelected(date) }
                            )
                        } else {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun buildMonthCells(referenceDate: LocalDate): List<List<LocalDate?>> {
    val yearMonth = YearMonth.from(referenceDate)
    val firstOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startIndex = firstOfMonth.dayOfWeek.ordinal // 0..6 (LUN..DOM)

    val totalCells = 42
    val cells = MutableList<LocalDate?>(totalCells) { null }

    for (day in 1..daysInMonth) {
        val cellIndex = startIndex + (day - 1)
        if (cellIndex in 0 until totalCells) {
            cells[cellIndex] = yearMonth.atDay(day)
        }
    }

    return cells.chunked(7)
}

@Composable
private fun MonthDayItem(
    date: LocalDate,
    isSelected: Boolean,
    hasEvents: Boolean,
    isInMonth: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable(enabled = isInMonth) { onClick() }
    ) {
        val dayNumber = date.dayOfMonth.toString()
        Box(
            modifier = Modifier
                .size(if (isSelected) 32.dp else 28.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        isSelected -> PrimaryBlue
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayNumber,
                color = when {
                    !isInMonth -> TextSecondary.copy(alpha = 0.4f)
                    isSelected -> TextPrimary
                    else -> TextPrimary
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (hasEvents) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PrimaryBlue)
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun MonthSummaryCard(
    title: String,
    subtitle: String,
    emoji: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "›",
            color = TextSecondary,
            fontSize = 18.sp
        )
    }
}


// ---------- TIRA DE DÍAS (LUN - DOM) ----------

@Composable
fun WeekDayRow(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Calculamos el lunes de la semana actual
    val firstDayOfWeek = selectedDate.with(DayOfWeek.MONDAY)
    // Generamos los 7 días
    val days = (0..6).map { firstDayOfWeek.plusDays(it.toLong()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { date ->
            WeekDayItem(
                date = date,
                selected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun WeekDayItem(
    date: LocalDate,
    selected: Boolean,
    onClick: () -> Unit
) {
    val dayLabel = date.dayOfWeek.name.take(3) // LUN, MAR...
    val dayNumber = date.dayOfMonth

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = dayLabel.lowercase().replaceFirstChar { it.uppercase() },
            color = TextSecondary,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Círculo indicador
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (selected) PrimaryBlue else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayNumber.toString(),
                color = if (selected) TextPrimary else TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ---------------------------------------------------------
//  CARD DE EVENTO REUTILIZABLE (vista diaria / mensual)
// ---------------------------------------------------------

@Composable
fun CalendarEventCard(
    event: CalendarUiEvent,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val bgColor = when (event.color) {
        CalendarEventColor.YELLOW -> Color(0xFF5E4716)
        CalendarEventColor.PURPLE -> Color(0xFF3D2A69)
        CalendarEventColor.GREEN -> Color(0xFF1E5C38)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .let { base ->
                if (onClick != null) base.clickable { onClick() } else base
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = event.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${event.timeLabel} - ${event.placeLabel}",
                color = TextPrimary.copy(alpha = 0.9f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (event.participants.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(-8.dp)
                ) {
                    event.participants.take(3).forEach { member ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(BackgroundDark.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.name.firstOrNull()?.uppercase() ?: "",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------
//  FAB "+" PARA NUEVO EVENTO
// ---------------------------------------------------------

@Composable
fun AddEventFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(64.dp)
            .shadow(10.dp, CircleShape),
        containerColor = PrimaryBlue
    ) {
        Text(
            text = "+",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ---------------------------------------------------------
//  DIALOGO "NUEVO EVENTO"
// ---------------------------------------------------------

@Composable
fun NewEventDialog(
    visible: Boolean,
    date: LocalDate,
    availableMembers: List<Member>,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, time: String, participants: List<Member>) -> Unit
) {
    if (!visible) return

    val titleState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }
    val timeState = remember { mutableStateOf("07:30 PM") }

    val selectedMembers = remember {
        mutableStateListOf<Member>().apply {
            availableMembers.take(2).forEach { add(it) }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardDark)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Nuevo Evento",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        val formatter =
                            DateTimeFormatter.ofPattern(
                                "EEEE, d 'de' MMMM yyyy",
                                Locale("es", "ES")
                            )
                        Text(
                            text = date.format(formatter)
                                .replaceFirstChar { it.uppercase() },
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "✕",
                        color = TextSecondary,
                        fontSize = 18.sp,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LabeledTextField(
                    label = "Título",
                    placeholder = "Ej. Cena familiar",
                    state = titleState
                )

                LabeledTextField(
                    label = "Descripción",
                    placeholder = "Añade más detalles...",
                    state = descriptionState,
                    singleLine = false,
                    minLines = 3
                )

                LabeledTextField(
                    label = "Hora",
                    placeholder = "07:30 PM",
                    state = timeState,
                    readOnly = false
                )

                Text(
                    text = "Participantes",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                ParticipantsSelector(
                    members = availableMembers,
                    selectedMembers = selectedMembers
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BackgroundDark)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancelar",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(PrimaryBlue)
                            .clickable {
                                onSave(
                                    titleState.value.trim(),
                                    descriptionState.value.trim(),
                                    timeState.value.trim(),
                                    selectedMembers.toList()
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Guardar Evento",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledTextField(
    label: String,
    placeholder: String,
    state: MutableState<String>,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = state.value,
            onValueChange = { state.value = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            readOnly = readOnly,
            colors = TextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryBlue,
                focusedContainerColor = BackgroundDark,
                unfocusedContainerColor = BackgroundDark,
                disabledContainerColor = BackgroundDark,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun ParticipantsSelector(
    members: List<Member>,
    selectedMembers: MutableList<Member>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        members.take(3).forEach { member ->
            val isSelected = selectedMembers.any { it.id == member.id }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) PrimaryBlue else BackgroundDark
                    )
                    .clickable {
                        if (isSelected) {
                            selectedMembers.removeAll { it.id == member.id }
                        } else {
                            selectedMembers.add(member)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.firstOrNull()?.uppercase() ?: "",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+", color = TextSecondary, fontSize = 18.sp)
        }
    }
}
