package com.iub.hometask.data.mock

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

enum class TaskCategory { COCINA, LIMPIEZA, JARDIN }

// Actualizado a inglés/estados nuevos para coincidir con tu código reciente
enum class TaskStatus { PENDING, IN_PROGRESS, DONE }

// Modelo para el Timeline
data class SubTask(
    val id: Int,
    val title: String,
    var status: TaskStatus
)

data class HomeTask(
    val id: Int,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val status: TaskStatus, // Estado general
    val dueLabel: String,   // "Hoy", "Mañana"
    val dueTime: String? = null,    // "22:00"
    val creationDate: String, // "15 de Jul, 2024"
    val assignedMemberIds: List<Int>,
    // Lista dinámica de sub-tareas para el Plan de Acción
    val subTasks: SnapshotStateList<SubTask> = mutableStateListOf()
)

object MockTasksRepository {
    val tasks: SnapshotStateList<HomeTask> = mutableStateListOf(
        HomeTask(
            id = 1,
            title = "Limpiar la cocina a fondo",
            description = "Asegurarse de limpiar todas las superficies, fregar el suelo, limpiar el microondas por dentro y organizar la despensa.",
            category = TaskCategory.COCINA,
            status = TaskStatus.IN_PROGRESS,
            dueLabel = "Hoy",
            dueTime = "22:00",
            creationDate = "15 de Jul, 2024",
            assignedMemberIds = listOf(1), // Juan Pérez
            subTasks = mutableStateListOf(
                SubTask(1, "Organizar la despensa", TaskStatus.IN_PROGRESS),
                SubTask(2, "Limpiar superficies y microondas", TaskStatus.PENDING),
                SubTask(3, "Fregar el suelo", TaskStatus.PENDING)
            )
        ),
        HomeTask(
            id = 2,
            title = "Sacar la basura",
            description = "Separar reciclaje y orgánicos.",
            category = TaskCategory.LIMPIEZA,
            status = TaskStatus.PENDING,
            dueLabel = "Mañana",
            dueTime = "08:00",
            creationDate = "16 de Jul, 2024",
            assignedMemberIds = listOf(2)
        ),
        HomeTask(
            id = 3,
            title = "Cortar el césped",
            description = "El jardín delantero y trasero.",
            category = TaskCategory.JARDIN,
            status = TaskStatus.DONE,
            dueLabel = "Ayer",
            dueTime = "10:00",
            creationDate = "14 de Jul, 2024",
            assignedMemberIds = listOf(1)
        )
    )

    fun getTaskById(id: Int): HomeTask? = tasks.find { it.id == id }

    // --- FUNCIONES QUE FALTABAN (RESTAURADAS Y ADAPTADAS) ---

    /**
     * Retorna tareas que NO están terminadas (PENDING o IN_PROGRESS).
     * Filtra por categoría si se proporciona.
     */
    fun getPendingTasks(categoryFilter: TaskCategory?): List<HomeTask> {
        return tasks.filter { task ->
            // Es pendiente si NO está DONE
            (task.status != TaskStatus.DONE) &&
                    // Y coincide con la categoría (si hay filtro)
                    (categoryFilter == null || task.category == categoryFilter)
        }
    }

    /**
     * Retorna tareas que están terminadas (DONE).
     * Filtra por categoría si se proporciona.
     */
    fun getCompletedTasks(categoryFilter: TaskCategory?): List<HomeTask> {
        return tasks.filter { task ->
            // Es completada si está DONE
            (task.status == TaskStatus.DONE) &&
                    // Y coincide con la categoría (si hay filtro)
                    (categoryFilter == null || task.category == categoryFilter)
        }
    }
}