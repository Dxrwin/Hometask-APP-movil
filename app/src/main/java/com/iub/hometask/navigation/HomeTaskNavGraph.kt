package com.iub.hometask.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
//import com.iub.hometask.data.mock.HomeTask
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.features.auth.Login.LoginScreen
import com.iub.hometask.features.auth.Signup.SignUpScreen
import com.iub.hometask.features.auth.Welcome.WelcomeScreen
import com.iub.hometask.features.calendar.CalendarScreen
import com.iub.hometask.features.calendar.EventDetailsScreen
import com.iub.hometask.features.chat.ChatScreen
import com.iub.hometask.features.chat.assistant.AssistantChatScreen
import com.iub.hometask.features.common.SimplePlaceholderScreen
import com.iub.hometask.features.loading.LoadingScreen
import com.iub.hometask.features.members.MembersScreen
import com.iub.hometask.features.panel.PanelNotificationType
import com.iub.hometask.features.panel.PanelScreen
import com.iub.hometask.features.profile.ProfileScreenNew
import com.iub.hometask.features.settings.SettingsScreen
import com.iub.hometask.features.tasks.TasksScreen
import com.iub.hometask.features.tasks.add.AddTaskScreen
import com.iub.hometask.features.tasks.details.TaskDetailsScreen

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"

    // Rutas Principales
    const val PANEL = "panel"                // Panel Admin
    const val MEMBER_PANEL = "member_panel"  // Panel Miembro (NUEVO)
    const val TASKS = "tasks"
    const val CALENDAR = "calendar"
    const val MEMBERS = "members"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    // Rutas Secundarias / Acciones
    const val ADD_TASK = "add_task"
    const val ASSISTANT_CHAT = "assistant_chat"

    // Rutas con Parámetros / Variaciones
    const val PANEL_FROM_LOGIN = "panel_from_login"
    const val PANEL_FROM_REGISTER = "panel_from_register"
    const val CHAT = "chat/{memberId}"
    const val LOADING = "loading/{target}"
    const val TASK_DETAILS = "task_details/{taskId}"
    const val EVENT_DETAILS = "event_details/{eventId}"

    // Helpers para construir rutas con parámetros
    fun taskDetails(taskId: Int) = "task_details/$taskId"
    fun eventDetails(eventId: Int) = "event_details/$eventId"
    fun chat(memberId: Int) = "chat/$memberId"
    fun loading(target: String) = "loading/$target"
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeTaskNavGraph(navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = Routes.WELCOME) {

            // ==================== AUTH ====================
            composable(route = Routes.WELCOME) {
                WelcomeScreen(
                    onLoginClick = { navController.navigate(Routes.LOGIN) },
                    onSignUpClick = { navController.navigate(Routes.loading(target = Routes.SIGN_UP)) }
                )
            }

            composable(route = Routes.LOGIN) {
                LoginScreen(
                    // Lógica ROL 1 (Admin) -> Panel Completo
                    onNavigateToAdminPanel = {
                        navController.navigate(Routes.loading(target = Routes.PANEL)) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    // Lógica ROL 2 (Miembro) -> Panel Limitado
                    onNavigateToMemberPanel = {
                        navController.navigate(Routes.loading(target = Routes.MEMBER_PANEL)) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = { navController.navigate(Routes.loading(target = Routes.SIGN_UP)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(route = Routes.SIGN_UP) {
                SignUpScreen(
                    onBackClick = { navController.popBackStack() },
                    onSignUpSuccess = {
                        // Al registrarse, enviamos al Login para obtener token y rol
                        navController.navigate(Routes.LOGIN)
                    },
                    onNavigateToLogin = { navController.navigate(Routes.loading(Routes.LOGIN)) }
                )
            }

            // ==================== UTILS ====================
            composable(
                route = Routes.LOADING,
                arguments = listOf(navArgument("target") { type = NavType.StringType })
            ) { backStackEntry ->
                val target = backStackEntry.arguments?.getString("target") ?: Routes.PANEL
                LoadingScreen(
                    onFinished = {
                        when (target) {
                            Routes.LOGIN -> navController.navigate(Routes.LOGIN) { popUpTo(Routes.WELCOME) { inclusive = false } }
                            Routes.SIGN_UP -> navController.navigate(Routes.SIGN_UP)
                            else -> navController.navigate(target) { popUpTo(Routes.WELCOME) { inclusive = true } }
                        }
                    }
                )
            }

            // ==================== ADMIN PANEL & FEATURES ====================

            // Función auxiliar para navegar desde el BottomBar sin apilar infinitamente
            val onNavigateBottomCommon: (String) -> Unit = { route ->
                if (route != Routes.PANEL) {
                    navController.navigate(route) {
                        // Mantiene el estado y evita duplicados
                        popUpTo(Routes.PANEL) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                } else {
                    // Si ya estamos en Panel o queremos volver a la raíz
                    navController.navigate(Routes.PANEL) {
                        popUpTo(Routes.PANEL) { inclusive = true }
                    }
                }
            }

            // 1. PANEL PRINCIPAL (Admin)
            composable(route = Routes.PANEL) {
                PanelScreen(
                    onNavigateToMembers = { navController.navigate(Routes.MEMBERS) },
                    onNavigateBottom = onNavigateBottomCommon,
                    initialNotification = null,
                    onOpenAddTask = { navController.navigate(Routes.ADD_TASK) },
                    onOpenAssistantChat = { navController.navigate(Routes.ASSISTANT_CHAT) },
                    onTaskSelected = { navController.navigate(Routes.taskDetails(0)) }, // ID 0 por defecto si no viene
                    onNavigateToChat = { route -> navController.navigate(route) },
                    onEventClick = { id ->
                        if (id < 100) navController.navigate(Routes.taskDetails(id))
                        else navController.navigate(Routes.eventDetails(id))
                    }
                )
            }

            // Variantes del Panel para mostrar Notificaciones (Login/Registro)
            composable(route = Routes.PANEL_FROM_LOGIN) {
                PanelScreen(
                    onNavigateToMembers = { navController.navigate(Routes.MEMBERS) },
                    onNavigateBottom = onNavigateBottomCommon,
                    initialNotification = PanelNotificationType.LOGIN,
                    onOpenAddTask = { navController.navigate(Routes.ADD_TASK) },
                    onOpenAssistantChat = { navController.navigate(Routes.ASSISTANT_CHAT) },
                    onTaskSelected = { navController.navigate(Routes.taskDetails(0)) },
                    onNavigateToChat = { route -> navController.navigate(route) },
                    onEventClick = { id -> navController.navigate(Routes.eventDetails(id)) }
                )
            }

            composable(route = Routes.PANEL_FROM_REGISTER) {
                PanelScreen(
                    onNavigateToMembers = { navController.navigate(Routes.MEMBERS) },
                    onNavigateBottom = onNavigateBottomCommon,
                    initialNotification = PanelNotificationType.REGISTER,
                    onOpenAddTask = { navController.navigate(Routes.ADD_TASK) },
                    onOpenAssistantChat = { navController.navigate(Routes.ASSISTANT_CHAT) },
                    onTaskSelected = { navController.navigate(Routes.taskDetails(0)) },
                    onNavigateToChat = { route -> navController.navigate(route) },
                    onEventClick = { id -> navController.navigate(Routes.eventDetails(id)) }
                )
            }

            // ==================== MEMBER PANEL (ROL 2) ====================
            composable(route = Routes.MEMBER_PANEL) {
                SimplePlaceholderScreen(
                    title = "Vista de Miembro del Hogar\n(Funcionalidad Limitada)",
                    currentRoute = Routes.PANEL, // Simulamos estar en Panel para UI
                    onNavigateBottom = { route ->
                        // Aquí defines a qué tiene acceso el Miembro (ej. Chat o Perfil)
                        if (route == Routes.PROFILE) navController.navigate(Routes.PROFILE)
                        // Si intenta ir a admin panel, no hace nada o muestra mensaje
                    }
                )
            }

            // ==================== COMMON FEATURES ====================

            composable(route = Routes.TASKS) {
                TasksScreen(
                    onTaskSelected = { task -> navController.navigate(Routes.taskDetails(task.id)) },
                    onNavigateBottom = onNavigateBottomCommon,
                    onAddTaskClick = { navController.navigate(Routes.ADD_TASK) }
                )
            }

            composable(route = Routes.ADD_TASK) {
                AddTaskScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateBottom = onNavigateBottomCommon
                )
            }

            composable(route = Routes.ASSISTANT_CHAT) {
                AssistantChatScreen(onBackClick = { navController.popBackStack() })
            }

            composable(route = Routes.PROFILE) {
                ProfileScreenNew( // Usando la versión "New" que tenías importada
                    onBackClick = { navController.popBackStack() },
                    onNavigateBottom = onNavigateBottomCommon
                )
            }

            composable(route = Routes.SETTINGS) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateBottom = onNavigateBottomCommon
                )
            }

            composable(route = Routes.MEMBERS) {
                MembersScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateBottom = onNavigateBottomCommon
                )
            }

            composable(route = Routes.CALENDAR) {
                CalendarScreen(
                    onNavigateBottom = onNavigateBottomCommon,
                    onEventClick = { id ->
                        if (id < 100) navController.navigate(Routes.taskDetails(id))
                        else navController.navigate(Routes.eventDetails(id))
                    },
                    animatedVisibilityScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }

            // ==================== DETAILS SCREENS ====================

            composable(
                route = Routes.TASK_DETAILS,
                arguments = listOf(navArgument("taskId") { type = NavType.IntType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
                TaskDetailsScreen(
                    taskId = taskId,
                    onBackClick = { navController.popBackStack() },
                    animatedVisibilityScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }

            composable(
                route = Routes.EVENT_DETAILS,
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
                EventDetailsScreen(
                    eventId = eventId,
                    onBackClick = { navController.popBackStack() },
                    animatedVisibilityScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }

            composable(
                route = Routes.CHAT,
                arguments = listOf(navArgument("memberId") { type = NavType.IntType })
            ) { backStackEntry ->
                val memberId = backStackEntry.arguments?.getInt("memberId") ?: 0
                val member = MockMembersRepository.members.find { it.id == memberId }
                    ?: MockMembersRepository.members.firstOrNull()

                if (member != null) {
                    ChatScreen(
                        member = member,
                        onBackClick = { navController.popBackStack() },
                        onNavigateBottom = onNavigateBottomCommon
                    )
                }
            }
        }
    }
}