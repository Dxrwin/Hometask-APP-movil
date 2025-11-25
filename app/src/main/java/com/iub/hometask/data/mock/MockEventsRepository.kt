package com.iub.hometask.data.mock


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.iub.hometask.ui.components.CalendarEventColor

data class HomeEvent(
    val id: Int,
    val title: String,
    val description: String,
    val dateLabel: String,  // "Sábado, 25 de mayo"
    val timeLabel: String,  // "10:00 AM"
    val location: String,   // "En casa de la abuela"
    val color: CalendarEventColor,
    val participantIds: List<Int>
)

object MockEventsRepository {
    val events: SnapshotStateList<HomeEvent> = mutableStateListOf(
        HomeEvent(
            id = 100, // IDs diferentes para no chocar con tareas
            title = "Limpieza Profunda de Primavera",
            description = "Una limpieza a fondo de toda la casa para preparar la llegada de la nueva temporada.",
            dateLabel = "Sábado, 25 de mayo",
            timeLabel = "10:00 AM",
            location = "Toda la casa",
            color = CalendarEventColor.YELLOW,
            participantIds = listOf(1, 2, 4) // Juan, Sara, Ana
        ),
        HomeEvent(
            id = 101,
            title = "Comida familiar",
            description = "Reunión mensual con los abuelos.",
            dateLabel = "Domingo, 26 de mayo",
            timeLabel = "02:00 PM",
            location = "Casa de la abuela",
            color = CalendarEventColor.PURPLE,
            participantIds = listOf(1, 2, 3, 4) // Todos
        )
    )

    fun getEventById(id: Int): HomeEvent? = events.find { it.id == id }
}