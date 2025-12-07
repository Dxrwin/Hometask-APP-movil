package com.iub.hometask.features.panel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark
import kotlinx.coroutines.launch

@Composable
fun PanelScreen(
    onNavigateToMembers: () -> Unit,
    onNavigateBottom: (String) -> Unit,
    initialNotification: PanelNotificationType? = null,
    onOpenAddTask: () -> Unit = {},
    onOpenAssistantChat: () -> Unit,
    onTaskSelected: () -> Unit = {}, // Podríamos usar esto para una tarea específica rápida
    onNavigateToChat: (String) -> Unit = {},
    // Necesitamos callback para ir al detalle del evento
    onEventClick: (Int) -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = Routes.PANEL

    // Estado para la notificación
    var showNotification by remember { mutableStateOf(initialNotification != null) }

    val notificationMessage = when (initialNotification) {
        PanelNotificationType.LOGIN -> "¡Bienvenido de nuevo! Iniciaste sesión correctamente."
        PanelNotificationType.REGISTER -> "¡Registro completado! Tu hogar está listo para usar."
        null -> ""
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Contenido principal del panel
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                HomeDrawerContent(
                    currentRoute = currentRoute,
                    onDestinationSelected = { route ->
                        scope.launch { drawerState.close() }
                        onNavigateBottom(route)
                    }
                )
            }
        ) {
            Scaffold(
                containerColor = BackgroundDark,
                topBar = {
                    DashboardTopBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onProfileClick = { onNavigateBottom(Routes.PROFILE) },
                        onChatClick = { onNavigateBottom(Routes.MEMBERS) },
                        onSettingsClick = { onNavigateBottom(Routes.SETTINGS) }
                    )
                },
                bottomBar = {
                    HomeBottomNavigationBar(
                        currentRoute = currentRoute,
                        onItemSelected = onNavigateBottom
                    )
                },
                floatingActionButton = {
                    PrimaryFab(onClick = onOpenAssistantChat)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // 1. PROGRESO DEL EQUIPO
                    TeamProgressSection()

                    // 2. TARJETAS DE ESTADÍSTICAS (TAREAS / MIEMBROS)
                    DashboardStatsGrid(
                        onTasksClick = {
                            // REDIRECCIÓN A INTERFAZ TAREAS DEL HOGAR
                            onNavigateBottom(Routes.TASKS)
                        },
                        onMembersClick = onNavigateToMembers
                    )

                    // 3. CRONOLOGÍA DE EVENTOS
                    EventTimelineSection(
                        onEventClick = { eventId ->
                            // REDIRECCIÓN A DETALLES DE EVENTO
                            onEventClick(eventId)
                        }
                    )

                    // 4. CALENDARIO MINIATURA
                    DashboardMiniCalendar(
                        onCalendarClick = {
                            onNavigateBottom(Routes.TASKS) // Redirige a Tareas al tocar calendario
                        }
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // NOTIFICACIÓN FLOTANTE - En el nivel más alto para garantizar visibilidad
        if (showNotification) {
            NotificationBanner(
                message = notificationMessage,
                visible = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                onDismiss = { showNotification = false }
            )
        }
    }
}





/*package com.iub.hometask.features.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark

import kotlinx.coroutines.launch


@Composable
fun PanelScreen(
    onNavigateToMembers: () -> Unit,
    onNavigateBottom: (String) -> Unit,
    initialNotification: PanelNotificationType? = null,
    onOpenAddTask: () -> Unit,
    onOpenAssistantChat: () -> Unit,
    onTaskSelected: () -> Unit, // <-- NUEVO (simple por ahora)
    onNavigateToChat: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = Routes.PANEL

    var showNotification by remember { mutableStateOf(initialNotification != null) }

    val notificationMessage = when (initialNotification) {
        PanelNotificationType.LOGIN -> "Inicio de sesión correcto. ¡Bienvenido de nuevo!"
        PanelNotificationType.REGISTER -> "Registro completado. Tu hogar está listo."
        null -> ""
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawerContent(
                currentRoute = currentRoute,
                onDestinationSelected = { route ->
                    scope.launch { drawerState.close() }
                    // Panel usa bottom nav para navegar
                    onNavigateBottom(route)
                }
            )
        }
    )
    {
        Scaffold(
            containerColor = BackgroundDark,
            topBar = {
                DashboardTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfileClick = { onNavigateBottom(Routes.PROFILE) },
                    onChatClick = { onNavigateBottom(Routes.MEMBERS) },
                    onSettingsClick = { onNavigateBottom(Routes.SETTINGS) }
                )
            },
            bottomBar = {
                HomeBottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemSelected = onNavigateBottom
                )
            },
            floatingActionButton = {
                PrimaryFab(
                    onClick = onOpenAssistantChat   // <--- FAB abre asistente
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                DashboardSummarySection(
                    onTaskClick = { onTaskSelected() }
                )

                DashboardRecentActivitySection()

                MembersSummarySection(
                    onManageClick = onNavigateToMembers,
                    onAddTaskClick = onOpenAddTask
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        }
    }*/

