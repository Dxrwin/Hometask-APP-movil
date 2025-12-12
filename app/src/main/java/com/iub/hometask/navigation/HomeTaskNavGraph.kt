package com.iub.hometask.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
//import com.iub.hometask.data.mock.HomeTask
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.data.repository.MemberUiModel
import com.iub.hometask.features.auth.Login.LoginScreen
import com.iub.hometask.features.auth.Signup.SignUpScreen
import com.iub.hometask.features.auth.Welcome.WelcomeScreen
import com.iub.hometask.features.calendar.CalendarScreen
import com.iub.hometask.features.calendar.EventDetailsScreen
import com.iub.hometask.features.chat.CameraScreen
import com.iub.hometask.features.chat.ChatScreen
import com.iub.hometask.features.chat.assistant.AssistantChatScreen
import com.iub.hometask.features.common.SimplePlaceholderScreen
import com.iub.hometask.features.gallery.GalleryScreen
import com.iub.hometask.features.loading.LoadingScreen
import com.iub.hometask.features.members.AddMemberScreen
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

    const val ADD_MEMBER = "add_member" // <--- NUEVA RUTA
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    // Rutas Secundarias / Acciones
    const val ADD_TASK = "add_task"
    const val ASSISTANT_CHAT = "assistant_chat"

    // Rutas con Parámetros / Variaciones
    const val PANEL_FROM_LOGIN = "panel_from_login"
    const val PANEL_FROM_REGISTER = "panel_from_register"
    const val CHAT = "chat/{memberId}"
    //const val LOADING = "loading/{target}"
    const val TASK_DETAILS = "task_details/{taskId}"
    const val EVENT_DETAILS = "event_details/{eventId}"

    const val CAMERA = "camera"

    const val GALLERY = "gallery"

    // Helpers para construir rutas con parámetros
    fun taskDetails(taskId: Int) = "task_details/$taskId"
    fun eventDetails(eventId: Int) = "event_details/$eventId"
    fun chat(memberId: Int) = "chat/$memberId"
    //fun loading(target: String) = "loading/$target"
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
                    onSignUpClick = { navController.navigate(Routes.SIGN_UP) }
                )
            }

            composable(route = Routes.LOGIN) {
                LoginScreen(
                    // Lógica ROL 1 (Admin) -> Panel Completo
                    onNavigateToAdminPanel = {
                        navController.navigate(Routes.PANEL) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    // Lógica ROL 2 (Miembro) -> Panel Limitado
                    onNavigateToMemberPanel = {
                        navController.navigate(Routes.MEMBER_PANEL) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) },
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
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
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
                    onNavigateBottom = onNavigateBottomCommon,
                    onAddMemberClick = { navController.navigate(Routes.ADD_MEMBER) },

                    // 1. Navegar al Perfil del Miembro
                    onMemberClick = { memberId ->
                        // Aquí deberías tener una ruta como "profile/{memberId}"
                        // Por ahora, si usas el perfil propio, puedes redirigir a PROFILE o crear MEMBER_PROFILE
                        navController.navigate(Routes.PROFILE) // Placeholder
                    },

                    // 2. Navegar al Chat
                    onChatClick = { memberId ->
                        navController.navigate(Routes.chat(memberId))
                    }
                )
            }

            composable(route = Routes.ADD_MEMBER) {
                AddMemberScreen(
                    onBackClick = { navController.popBackStack() },
                    onMemberCreated = {
                        navController.popBackStack() // Volver a la lista tras crear
                    }
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

            // 1. Agregar la pantalla de Cámara
            composable(Routes.CAMERA) {
                CameraScreen(
                    onClose = { navController.popBackStack() },
                    onImageCaptured = { uri ->
                        // Guardamos el resultado para el ChatScreen
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("captured_image_uri", uri)
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.GALLERY) {
                GalleryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onImageSelected = { uri ->
                        // Guardamos el resultado para el ChatScreen
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("gallery_selected_uri", uri)
                        navController.popBackStack()
                    }
                )
            }

            // Ruta Chat debe aceptar ID
            composable(
                route = Routes.CHAT,
                arguments = listOf(navArgument("memberId") { type = NavType.IntType })
            ) { backStackEntry ->
                val memberId = backStackEntry.arguments?.getInt("memberId") ?: 0

                // TRUCO RÁPIDO: Crea un modelo temporal con el ID para pasarlo al ChatScreen
                // El ChatScreen cargará los mensajes usando ese ID.
                // Para el nombre y foto, lo ideal sería que el ChatViewModel también cargue "MemberDetails".
                // Por ahora pondremos un nombre placeholder o pasaremos los argumentos por URL si es posible.

                val tempMember = MemberUiModel(
                    id = memberId,
                    name = "Usuario $memberId",
                    email = "",
                    roleName = "",
                    imageUrl = null
                )

                ChatScreen(
                    member = tempMember, // El ViewModel se encargará de los mensajes
                    onBackClick = { navController.popBackStack() },
                    navController = navController,
                    onNavigateBottom = onNavigateBottomCommon
                )
            }
        }
    }
}