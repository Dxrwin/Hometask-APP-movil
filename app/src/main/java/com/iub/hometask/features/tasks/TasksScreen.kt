package com.iub.hometask.features.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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

    var selectedCategory by remember { mutableStateOf<TaskCategory?>(null) }

    val pendingTasks = remember(selectedCategory) {
        MockTasksRepository.getPendingTasks(selectedCategory)
    }
    val completedTasks = remember(selectedCategory) {
        MockTasksRepository.getCompletedTasks(selectedCategory)
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TasksTopBar(
                onAddTaskClick = onAddTaskClick
            )
        },
        bottomBar = {
            HomeBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = onNavigateBottom
            )
        },
        floatingActionButton = {
            PrimaryFab {
                // aquí puedes navegar directo al asistente
                onNavigateBottom(Routes.PANEL) // si ahí ya llamas a ASSISTANT
                // o pasa un callback onOpenAssistantChat similar al de PanelScreen
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            TaskCategoryFilterRow(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TasksSectionCard(
                title = "Pendientes",
                tasks = pendingTasks,
                members = members,
                onTaskClick = { task ->
                    onTaskSelected(task)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TasksSectionCard(
                title = "Completadas",
                tasks = completedTasks,
                members = members,
                onTaskClick = { task ->
                    onTaskSelected(task)
                }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
