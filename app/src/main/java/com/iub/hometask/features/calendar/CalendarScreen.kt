package com.iub.hometask.features.calendar

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.data.mock.MockEventsRepository
import com.iub.hometask.data.mock.MockEventsRepository.events
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.data.mock.MockTasksRepository
import com.iub.hometask.data.mock.TaskCategory
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CalendarScreen(
    onNavigateBottom: (String) -> Unit,
    onEventClick: (Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    val currentRoute = Routes.CALENDAR

    var mode by remember { mutableStateOf(CalendarMode.WEEK) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Mapa de eventos agrupados por Fecha
    val eventsByDate = remember {
        mutableStateMapOf<LocalDate, MutableList<CalendarUiEvent>>()
    }

    // --- LÓGICA DE CARGA Y UNIFICACIÓN DE DATOS ---
    LaunchedEffect(Unit) {
        // Limpiamos para evitar duplicados si recompone
        eventsByDate.clear()

        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        // 1. CARGAR TAREAS (Desde MockTasksRepository)
        // Para efectos de DEMO, asignaremos tareas impares a HOY y pares a MAÑANA
        MockTasksRepository.tasks.forEach { task ->
            val targetDate = if (task.id % 2 != 0) today else tomorrow

            // Determinamos el color visual según la categoría
            val color = when (task.category) {
                TaskCategory.COCINA -> CalendarEventColor.YELLOW
                TaskCategory.LIMPIEZA -> CalendarEventColor.GREEN // Usamos Green para limpieza
                TaskCategory.JARDIN -> CalendarEventColor.GREEN
            }

            // Convertimos HomeTask -> CalendarUiEvent
            val uiEvent = CalendarUiEvent(
                id = task.id, // Mantenemos ID original (ej: 1, 2, 3) -> < 100 es Tarea
                title = task.title,
                description = task.description,
                timeLabel = task.dueTime.toString(),
                placeLabel = "Tarea de ${task.category.name?.lowercase(Locale.ROOT) ?: "desconocida"}",
                participants = task.assignedMemberIds.mapNotNull {
                    MockMembersRepository.getMemberById(
                        it
                    )
                },
                color = color
            )

            // Añadir al mapa
            val list = eventsByDate.getOrPut(targetDate) { mutableListOf() }
            list.add(uiEvent)
        }

        // 2. CARGAR EVENTOS (Desde MockEventsRepository)
        // Para la DEMO, ponemos el evento 100 HOY y el 101 MAÑANA
        MockEventsRepository.events.forEach { event ->
            val targetDate = if (event.id == 100) today else tomorrow

            // Convertimos HomeEvent -> CalendarUiEvent
            val uiEvent = CalendarUiEvent(
                id = event.id, // ID original (ej: 100, 101) -> >= 100 es Evento
                title = event.title,
                description = event.description,
                timeLabel = event.timeLabel,
                placeLabel = event.location,
                participants = event.participantIds.mapNotNull {
                    MockMembersRepository.getMemberById(
                        it
                    )
                },
                color = event.color // Usamos el color definido en el evento (Purple/Yellow)
            )

            val list = eventsByDate.getOrPut(targetDate) { mutableListOf() }
            list.add(uiEvent)
        }
    }

    // Formateadores de fecha para la UI
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale("es", "ES"))
    val monthLabel = selectedDate.format(monthFormatter).replaceFirstChar { it.uppercase() }

    // Obtener la lista para el día seleccionado
    val todayEvents = eventsByDate[selectedDate] ?: emptyList()

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CalendarTopBar(
                title = monthLabel,
                onSearchClick = { /* TODO */ },
                onFilterClick = { /* TODO */ }
            )
        },
        bottomBar = {
            HomeBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = onNavigateBottom
            )
        },
        floatingActionButton = {
            // Aquí podrías decidir si el FAB crea tarea o evento
            AddEventFab(onClick = { /* Abrir diálogo */ })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Selector Líquido
            CalendarModeToggle(
                mode = mode,
                onModeChange = { mode = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tira de días
            WeekDayRow(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            //@OptIn(ExperimentalSharedTransitionApi::class)
            //@Composable
            with(sharedTransitionScope) {
                CalendarDayView(
                    date = selectedDate,
                    events = todayEvents,
                    onEventClick = { eventUi -> onEventClick(eventUi.id) },
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier
                )
                {
                    val todayEvents = eventsByDate[selectedDate] ?: emptyList()
                    val formatter =
                        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
                    val dateText = selectedDate.format(formatter).replaceFirstChar { it.uppercase() }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Encabezado de la fecha
                        Text(
                            text = dateText,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))

                        // Subtítulo (ej: "2 tareas, 1 evento")
                        Text(
                            text = "${todayEvents.size} tareas/eventos",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // itemsIndexed permite la animación de entrada en cascada
                            itemsIndexed(todayEvents) { index, event ->

                                // --- LÓGICA DE CLAVES PARA HERO TRANSITION ---
                                // Si el ID < 100 es Tarea, si es >= 100 es Evento (Según tu lógica de Mocks)
                                val heroKey =
                                    if (event.id < 100) "task-${event.id}" else "event-${event.id}"

                                Box(
                                    modifier = Modifier.animateEnter(index) // Efecto Cascada (Staggered)
                                ) {
                                    CalendarEventCard(
                                        event = event,
                                        onClick = { onEventClick(event.id) },
                                        modifier = Modifier.sharedElement(
                                            rememberSharedContentState(key = heroKey),
                                            animatedVisibilityScope
                                        )
                                    )
                                }
                            }
                        }

                        // Espacio al final para que el FAB no tape el último elemento
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}





/*package com.iub.hometask.features.calendar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarScreen(
    onNavigateBottom: (String) -> Unit,
    onOpenEventDetails: () -> Unit
) {
    val currentRoute = Routes.CALENDAR

    var mode by remember { mutableStateOf(CalendarMode.WEEK) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Eventos por fecha (en memoria)
    val eventsByDate = remember {
        mutableStateMapOf<LocalDate, MutableList<CalendarUiEvent>>()
    }

    // Mock de eventos de ejemplo (semana completa)
    LaunchedEffect(Unit) {
        val members = MockMembersRepository.members
        val weekStart = selectedDate.with(DayOfWeek.MONDAY)
        val mon = weekStart          // LUN
        val tue = weekStart.plusDays(1)
        val wed = weekStart.plusDays(2)
        val fri = weekStart.plusDays(4)
        val sun = weekStart.plusDays(6)

        eventsByDate.clear()

        // Martes
        eventsByDate[tue] = mutableListOf(
            CalendarUiEvent(
                id = 1,
                title = "Comprar víveres",
                description = "Lista en la nevera",
                timeLabel = "17:00",
                placeLabel = "Lista en la nevera",
                participants = listOf(members[0]),
                color = CalendarEventColor.PURPLE,
                iconEmoji = "🛒"
            )
        )

        // Miércoles
        eventsByDate[wed] = mutableListOf(
            CalendarUiEvent(
                id = 2,
                title = "Sacar el reciclaje",
                description = "Contenedor azul",
                timeLabel = "18:00",
                placeLabel = "Contenedor azul",
                participants = listOf(members[1]),
                color = CalendarEventColor.GREEN,
                iconEmoji = "♻️"
            )
        )

        // Viernes
        eventsByDate[fri] = mutableListOf(
            CalendarUiEvent(
                id = 3,
                title = "Noche de película",
                description = "20:00 - Salón",
                timeLabel = "20:00",
                placeLabel = "Salón",
                participants = listOf(members[2]),
                color = CalendarEventColor.PURPLE,
                iconEmoji = "🎬"
            )
        )

        // Domingo (tres eventos)
        eventsByDate[sun] = mutableListOf(
            CalendarUiEvent(
                id = 4,
                title = "Limpieza semanal",
                description = "Toda la casa",
                timeLabel = "10:00",
                placeLabel = "Toda la casa",
                participants = listOf(members[0], members[1]),
                color = CalendarEventColor.YELLOW,
                iconEmoji = "🧹"
            ),
            CalendarUiEvent(
                id = 5,
                title = "Comida familiar",
                description = "En casa de la abuela",
                timeLabel = "14:30",
                placeLabel = "Casa de la abuela",
                participants = listOf(members[1]),
                color = CalendarEventColor.PURPLE,
                iconEmoji = "🎉"
            ),
            CalendarUiEvent(
                id = 6,
                title = "Pasear al perro",
                description = "Parque central",
                timeLabel = "18:00",
                placeLabel = "Parque central",
                participants = listOf(members[2]),
                color = CalendarEventColor.GREEN,
                iconEmoji = "🐾"
            )
        )

        // Aseguramos que el día seleccionado tenga algo
        if (!eventsByDate.containsKey(selectedDate)) {
            eventsByDate[selectedDate] = mutableListOf()
        }
    }

    var showNewEventDialog by remember { mutableStateOf(false) }

    val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale("es", "ES"))
    val monthLabel = selectedDate.format(monthFormatter)
        .replaceFirstChar { it.uppercase() }

    val weekStart = selectedDate.with(DayOfWeek.MONDAY)

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CalendarTopBar(
                title = monthLabel,
                onSearchClick = { /* TODO: buscador */ },
                onFilterClick = { /* TODO: filtros */ }
            )
        },
        bottomBar = {
            HomeBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = onNavigateBottom
            )
        },
        floatingActionButton = {
            // En calendario, el FAB es para crear eventos
            AddEventFab(
                onClick = { showNewEventDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            CalendarModeToggle(
                mode = mode,
                onModeChange = { mode = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = mode,
                label = "calendar-mode",
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { currentMode ->
                when (currentMode) {
                    CalendarMode.WEEK -> {
                        CalendarWeekView(
                            weekStart = weekStart,
                            eventsByDate = eventsByDate.mapValues { it.value.toList() },
                            onEventClick = { onOpenEventDetails() }
                        )
                    }

                    CalendarMode.DAY -> {
                        val dayEvents = eventsByDate[selectedDate].orEmpty()
                        CalendarDayView(
                            date = selectedDate,
                            events = dayEvents,
                            onEventClick = { onOpenEventDetails() }
                        )
                    }

                    CalendarMode.MONTH -> {
                        CalendarMonthView(
                            currentDate = selectedDate,
                            selectedDate = selectedDate,
                            eventsByDate = eventsByDate.mapValues { it.value.toList() },
                            onDateSelected = { selectedDate = it },
                            onEventClick = { onOpenEventDetails() }
                        )
                    }
                }
            }
        }

        // Dialogo "Nuevo Evento"
        NewEventDialog(
            visible = showNewEventDialog,
            date = selectedDate,
            availableMembers = MockMembersRepository.members,
            onDismiss = { showNewEventDialog = false },
            onSave = { title, description, time, participants ->
                if (title.isNotBlank()) {
                    val listForDay =
                        eventsByDate.getOrPut(selectedDate) { mutableListOf() }
                    val nextId = (listForDay.maxOfOrNull { it.id } ?: 0) + 1
                    listForDay.add(
                        CalendarUiEvent(
                            id = nextId,
                            title = title,
                            description = description,
                            timeLabel = time.ifBlank { "00:00" },
                            placeLabel = description.ifBlank { "Evento del hogar" },
                            participants = participants,
                            color = CalendarEventColor.GREEN,
                            iconEmoji = "📌"
                        )
                    )
                }
                showNewEventDialog = false
            }
        )
    }
}*/
