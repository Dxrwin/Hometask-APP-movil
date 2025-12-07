package com.iub.hometask.features.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.data.mock.MockTasksRepository
import com.iub.hometask.data.mock.HomeTask
import com.iub.hometask.data.mock.TaskCategory
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark

@Composable
fun TasksScreen(
    onTaskSelected: (HomeTask) -> Unit,
    onNavigateBottom: (String) -> Unit,
    onAddTaskClick: () -> Unit
) {
    val currentRoute = Routes.TASKS
    val members = MockMembersRepository.members

    // Filtramos por categorías null para mostrar TODAS inicialmente
    // (Podrías agregar los chips de filtro arriba si quieres mantenerlos,
    // pero en la imagen de referencia no se ven chips, así que los quité para ser fiel al diseño).
    val pendingTasks = MockTasksRepository.getPendingTasks(null)
    val completedTasks = MockTasksRepository.getCompletedTasks(null)

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TasksTopBar(onAddTaskClick = onAddTaskClick)
        },
        bottomBar = {
            HomeBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = onNavigateBottom
            )
        },
        floatingActionButton = {
            PrimaryFab {
                // Acción del robot
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. CLASIFICACIÓN (Tarjetas Rojas/Amarillas)
            TaskClassificationSection()

            // 2. CARRUSEL MIEMBROS
            MembersCarousel(members = members)

            // 3. TODAS LAS TAREAS (Pendientes)
            TaskListSection(
                title = "Todas las Tareas",
                tasks = pendingTasks,
                members = members,
                onTaskClick = onTaskSelected
            )

            // 4. COMPLETADAS
            TaskListSection(
                title = "Completadas",
                tasks = completedTasks,
                members = members,
                onTaskClick = onTaskSelected
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}