package com.iub.hometask.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iub.hometask.data.mock.HomeTask
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.features.auth.Login.LoginScreen
import com.iub.hometask.features.auth.Signup.SignUpScreen
import com.iub.hometask.features.auth.Welcome.WelcomeScreen
import com.iub.hometask.features.calendar.CalendarScreen
import com.iub.hometask.features.calendar.EventDetailsScreen
import com.iub.hometask.features.chat.ChatScreen
import com.iub.hometask.features.chat.assistant.AssistantChatScreen
import com.iub.hometask.features.loading.LoadingScreen
import com.iub.hometask.features.members.MembersScreen
import com.iub.hometask.features.panel.PanelNotificationType
import com.iub.hometask.features.panel.PanelScreen
import com.iub.hometask.features.profile.ProfileScreen
import com.iub.hometask.features.tasks.TasksScreen
import com.iub.hometask.features.tasks.add.AddTaskScreen
import com.iub.hometask.features.tasks.details.TaskDetailsScreen

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val PROFILE = "profile"
    const val ASSISTANT_CHAT = "assistant_chat"
    const val PANEL = "panel"
    const val PANEL_FROM_LOGIN = "panel_from_login"
    const val PANEL_FROM_REGISTER = "panel_from_register"
    const val TASKS = "tasks"
    const val ADD_TASK = "add_task"
    const val CALENDAR = "calendar"
    const val MEMBERS = "members"
    const val CHAT = "chat/{memberId}"
    const val LOADING = "loading/{target}"

    // RUTAS DE DETALLE
    const val TASK_DETAILS = "task_details/{taskId}"
    fun taskDetails(taskId: Int) = "task_details/$taskId"

    const val EVENT_DETAILS = "event_details/{eventId}"
    fun eventDetails(eventId: Int) = "event_details/$eventId"

    fun chat(memberId: Int) = "chat/$memberId"
    fun loading(target: String) = "loading/$target"
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeTaskNavGraph(navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = Routes.WELCOME) {

            // ---------- WELCOME ----------
            composable(route = Routes.WELCOME) { backStackEntry ->
                WelcomeScreen(
                    onLoginClick = {
                        navController.navigate(Routes.LOGIN)
                    },
                    onSignUpClick = {
                        // si quieres que pase por la pantalla de loading
                        navController.navigate(
                            Routes.loading(target = Routes.SIGN_UP)
                        )
                    }
                )
            }

            // ---------- LOGIN ----------
            composable(route = Routes.LOGIN) { backStackEntry ->
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.PANEL) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate(
                            Routes.loading(target = Routes.SIGN_UP)
                        )
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ---------- SIGN UP ----------
            composable(route = Routes.SIGN_UP) {
                SignUpScreen(
                    onBackClick = { navController.popBackStack() },
                    onSignUpSuccess = {
                        // registro correcto -> loading -> panel con notificación de registro
                        navController.navigate(Routes.loading(Routes.PANEL_FROM_REGISTER)) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        // enlace "¿Ya tienes una cuenta? Iniciar sesión" -> loading -> perfil
                        navController.navigate(Routes.loading(Routes.PROFILE))
                    }
                )
            }

            // ---------- LOADING ----------
            composable(route = Routes.LOADING,
                arguments = listOf(
                    navArgument("target") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val target = backStackEntry.arguments?.getString("target") ?: Routes.PANEL

                LoadingScreen(
                    onFinished = {
                        when (target) {
                            Routes.PANEL,
                            Routes.PANEL_FROM_LOGIN,
                            Routes.PANEL_FROM_REGISTER,
                            Routes.MEMBERS,
                            Routes.TASKS,
                            Routes.CALENDAR -> {
                                navController.navigate(target) {
                                    popUpTo(Routes.WELCOME) { inclusive = true }
                                }
                            }
                            Routes.SIGN_UP -> {
                                navController.navigate(Routes.SIGN_UP) {
                                    popUpTo(Routes.WELCOME) { inclusive = false }
                                }
                            }
                            Routes.PROFILE -> {
                                navController.navigate(Routes.PROFILE) {
                                    popUpTo(Routes.WELCOME) { inclusive = true }
                                }
                            }
                            else -> {
                                navController.navigate(Routes.PANEL) {
                                    popUpTo(Routes.WELCOME) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }

            // ---------- PANEL ----------
            composable(route = Routes.PANEL) {
                PanelScreen(
                    onNavigateToMembers = { navController.navigate(Routes.MEMBERS) },
                    onNavigateBottom = { route ->
                        when (route) {
                            Routes.PANEL -> { /* ya estás aquí */ }
                            Routes.TASKS -> navController.navigate(Routes.TASKS)
                            Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                            Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                        }
                    },
                    onEventClick = { id ->
                        navController.navigate(Routes.eventDetails(id))
                    },
                    initialNotification = null, // o PanelNotificationType.LOGIN / REGISTER si es ese caso
                    onOpenAddTask = {               // <--- NUEVO
                        navController.navigate(Routes.ADD_TASK)
                    },
                    onOpenAssistantChat = {         // <--- NUEVO
                        navController.navigate(Routes.ASSISTANT_CHAT)
                    },
                    onTaskSelected = {
                        navController.navigate(Routes.TASK_DETAILS)
                    },
                    onNavigateToChat = { route ->
                        // si vas a usar el drawer para rutas tipo "chat/..."
                        navController.navigate(route)
                    }
                )
            }


            composable(route = Routes.PANEL_FROM_LOGIN) {
                PanelScreen(
                    onNavigateToMembers = { navController.navigate(Routes.MEMBERS) },
                    onNavigateBottom = { route ->
                        when (route) {
                            Routes.PANEL,
                            Routes.PANEL_FROM_LOGIN,
                            Routes.PANEL_FROM_REGISTER -> navController.navigate(Routes.PANEL)
                            Routes.TASKS -> navController.navigate(Routes.TASKS)
                            Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                            Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                        }
                    },

                    initialNotification = PanelNotificationType.LOGIN,
                    onOpenAddTask = {               // <--- NUEVO
                        navController.navigate(Routes.ADD_TASK)
                    },
                    onOpenAssistantChat = {         // <--- NUEVO
                        navController.navigate(Routes.ASSISTANT_CHAT)
                    },
                    onTaskSelected = {
                        navController.navigate(Routes.TASK_DETAILS)
                    },
                    onNavigateToChat = { route ->
                        // si vas a usar el drawer para rutas tipo "chat/..."
                        navController.navigate(route)
                    }
                )
            }

            composable(route = Routes.PANEL_FROM_REGISTER) {
                PanelScreen(
                    onNavigateToMembers = { navController.navigate(Routes.MEMBERS) },
                    onNavigateBottom = { route ->
                        when (route) {
                            Routes.PANEL,
                            Routes.PANEL_FROM_LOGIN,
                            Routes.PANEL_FROM_REGISTER -> navController.navigate(Routes.PANEL)
                            Routes.TASKS -> navController.navigate(Routes.TASKS)
                            Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                            Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                        }
                    },

                    initialNotification = PanelNotificationType.REGISTER,
                    onOpenAddTask = {               // <--- NUEVO
                        navController.navigate(Routes.ADD_TASK)
                    },
                    onOpenAssistantChat = {         // <--- NUEVO
                        navController.navigate(Routes.ASSISTANT_CHAT)
                    },
                    onTaskSelected = {
                        navController.navigate(Routes.TASK_DETAILS)
                    },
                    onNavigateToChat = { route ->
                        // si vas a usar el drawer para rutas tipo "chat/..."
                        navController.navigate(route)
                    }
                )
            }

            // ---------- MEMBERS ----------
            composable(route = Routes.MEMBERS) {
                MembersScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateBottom = { route ->
                        when (route) {
                            Routes.PANEL -> navController.navigate(Routes.PANEL)
                            Routes.TASKS -> navController.navigate(Routes.TASKS)
                            Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                            Routes.MEMBERS -> { /* ya estás aquí */ }
                        }
                    }
                )
            }

            // ---------- TASKS / CALENDAR / PROFILE ----------
            // ---------- TASKS ----------
            composable(route = Routes.TASKS) {
                TasksScreen(
                    onTaskSelected = { task: HomeTask ->
                        navController.navigate(Routes.TASK_DETAILS)
                    },
                    onNavigateBottom = { route ->
                        when (route) {
                            Routes.PANEL -> navController.navigate(Routes.PANEL)
                            Routes.TASKS -> {
                                navController.navigate(Routes.TASKS) {
                                    popUpTo(Routes.TASKS) { inclusive = true }
                                }
                            }
                            Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                            Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                        }
                    },
                    onAddTaskClick = {
                        navController.navigate(Routes.ADD_TASK)
                    }
                )
            }

            // ---------- CREAR / AÑADIR TAREA ----------
            composable(route = Routes.ADD_TASK) {
                AddTaskScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateBottom = { route ->
                        when (route) {
                            Routes.PANEL -> navController.navigate(Routes.PANEL)
                            Routes.TASKS -> navController.navigate(Routes.TASKS)
                            Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                            Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                        }
                    }
                )
            }

            // ---------- CHAT ASISTENTE IA ----------
            composable(route = Routes.ASSISTANT_CHAT) {
                AssistantChatScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }





            composable(route = Routes.PROFILE) {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ---------- TASK DETAILS ----------
            /*composable(route = Routes.TASK_DETAILS) {
                TaskDetailsScreen(
                    onBackClick = { navController.popBackStack() },
                    onOpenChatForAssignee = { memberId ->
                        navController.navigate(Routes.chat(memberId))
                    }
                    onNavigateBottom = { route ->
                        when (route) {
                            Routes.PANEL -> navController.navigate(Routes.PANEL)
                            Routes.TASKS -> navController.navigate(Routes.TASKS)
                            Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                            Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                        }
                    }
                )
            }*/

            // ---------- CHAT ----------
            composable(
                route = Routes.CHAT,
                arguments = listOf(navArgument("memberId") { type = NavType.IntType })
            ) { backStackEntry ->
                val memberId = backStackEntry.arguments?.getInt("memberId") ?: 1
                val member = MockMembersRepository.getMemberById(memberId)
                    ?: MockMembersRepository.members.first()

                ChatScreen(
                    member = member,
                    onBackClick = { navController.popBackStack() },
                    onNavigateBottom = { route ->
                        when (route) {
                            Routes.PANEL -> navController.navigate(Routes.PANEL)
                            Routes.TASKS -> navController.navigate(Routes.TASKS)
                            Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                            Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                        }
                    }
                )
            }

            // ... (Tus rutas de LOGIN, SIGN_UP, WELCOME, LOADING, PANEL, MEMBERS, CHAT se mantienen igual) ...
            // He omitido el código repetitivo para enfocarme en la lógica nueva.
            // ASUME QUE EL CÓDIGO ANTERIOR ESTÁ AQUÍ.

            // ---------- CALENDAR ----------
            composable(route = Routes.CALENDAR) {
                CalendarScreen(
                    onNavigateBottom = { route ->
                        if (route != Routes.CALENDAR) navController.navigate(route)
                    },
                    // Lógica para diferenciar click
                    onEventClick = { id ->
                        // Convención simple: IDs < 100 son Tareas, >= 100 son Eventos
                        // En una app real, pasarías un objeto o un enum Type
                        if (id < 100) {
                            navController.navigate(Routes.taskDetails(id))
                        } else {
                            navController.navigate(Routes.eventDetails(id))
                        }
                    },
                    animatedVisibilityScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }

            // ---------- DETALLE TAREA (TIMELINE) ----------
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

            // ---------- DETALLE EVENTO (PARTICIPANTES) ----------
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
        }
    }
}





/*package com.iub.hometask.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.features.chat.ChatScreen
import com.iub.hometask.features.tasks.details.TaskDetailsScreen
import com.iub.hometask.features.auth.Login.LoginScreen
import com.iub.hometask.features.auth.Signup.SignUpScreen
import com.iub.hometask.features.auth.Welcome.WelcomeScreen
import com.iub.hometask.features.common.SimplePlaceholderScreen
import com.iub.hometask.features.loading.LoadingScreen
import com.iub.hometask.features.members.MembersScreen
import com.iub.hometask.features.tasks.TasksScreen
import com.iub.hometask.features.tasks.add.AddTaskScreen
import com.iub.hometask.data.mock.HomeTask
import com.iub.hometask.features.calendar.CalendarScreen
import com.iub.hometask.features.calendar.EventDetailsScreen
import com.iub.hometask.features.chat.assistant.AssistantChatScreen
import com.iub.hometask.features.panel.PanelNotificationType
import com.iub.hometask.features.panel.PanelScreen
import com.iub.hometask.features.profile.ProfileScreen



object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val PROFILE = "profile"

    const val ASSISTANT_CHAT = "assistant_chat"

    // panel principal
    const val PANEL = "panel"
    const val PANEL_FROM_LOGIN = "panel_from_login"
    const val PANEL_FROM_REGISTER = "panel_from_register"

    const val TASKS = "tasks"

    const val ADD_TASK = "add_task"
    const val CALENDAR = "calendar"
    const val EVENT_DETAILS = "event_details"
    const val MEMBERS = "members"

    const val TASK_DETAILS = "task_details"
    //const val CHAT = "chat"

    const val CHAT = "chat/{memberId}"
    fun chat(memberId: Int) = "chat/$memberId"

    // loading genérico con destino
    const val LOADING = "loading/{target}"
    fun loading(target: String) = "loading/$target"
}



@Composable
fun HomeTaskNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        // ---------- WELCOME ----------
        composable(route = Routes.WELCOME) { backStackEntry ->
            WelcomeScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                },
                onSignUpClick = {
                    // si quieres que pase por la pantalla de loading
                    navController.navigate(
                        Routes.loading(target = Routes.SIGN_UP)
                    )
                }
            )
        }

        // ---------- LOGIN ----------
        composable(route = Routes.LOGIN) { backStackEntry ->
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.PANEL) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(
                        Routes.loading(target = Routes.SIGN_UP)
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // ---------- SIGN UP ----------
        composable(route = Routes.SIGN_UP) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpSuccess = {
                    // registro correcto -> loading -> panel con notificación de registro
                    navController.navigate(Routes.loading(Routes.PANEL_FROM_REGISTER)) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    // enlace "¿Ya tienes una cuenta? Iniciar sesión" -> loading -> perfil
                    navController.navigate(Routes.loading(Routes.PROFILE))
                }
            )
        }

        // ---------- LOADING ----------
        composable(route = Routes.LOADING,
            arguments = listOf(
                navArgument("target") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val target = backStackEntry.arguments?.getString("target") ?: Routes.PANEL

            LoadingScreen(
                onFinished = {
                    when (target) {
                        Routes.PANEL,
                        Routes.PANEL_FROM_LOGIN,
                        Routes.PANEL_FROM_REGISTER,
                        Routes.MEMBERS,
                        Routes.TASKS,
                        Routes.CALENDAR -> {
                            navController.navigate(target) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
                        }
                        Routes.SIGN_UP -> {
                            navController.navigate(Routes.SIGN_UP) {
                                popUpTo(Routes.WELCOME) { inclusive = false }
                            }
                        }
                        Routes.PROFILE -> {
                            navController.navigate(Routes.PROFILE) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate(Routes.PANEL) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // ---------- PANEL ----------
        composable(route = Routes.PANEL) {
            PanelScreen(
                onNavigateToMembers = { navController.navigate(Routes.MEMBERS) },
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL -> { /* ya estás aquí */ }
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                },
                initialNotification = null, // o PanelNotificationType.LOGIN / REGISTER si es ese caso
                onOpenAddTask = {               // <--- NUEVO
                    navController.navigate(Routes.ADD_TASK)
                },
                onOpenAssistantChat = {         // <--- NUEVO
                    navController.navigate(Routes.ASSISTANT_CHAT)
                },
                onTaskSelected = {
                    navController.navigate(Routes.TASK_DETAILS)
                },
                onNavigateToChat = { route ->
                    // si vas a usar el drawer para rutas tipo "chat/..."
                    navController.navigate(route)
                }
            )
        }


        composable(route = Routes.PANEL_FROM_LOGIN) {
            PanelScreen(
                onNavigateToMembers = { navController.navigate(Routes.MEMBERS) },
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL,
                        Routes.PANEL_FROM_LOGIN,
                        Routes.PANEL_FROM_REGISTER -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                },

                initialNotification = PanelNotificationType.LOGIN,
                onOpenAddTask = {               // <--- NUEVO
                    navController.navigate(Routes.ADD_TASK)
                },
                onOpenAssistantChat = {         // <--- NUEVO
                    navController.navigate(Routes.ASSISTANT_CHAT)
                },
                onTaskSelected = {
                    navController.navigate(Routes.TASK_DETAILS)
                },
                onNavigateToChat = { route ->
                    // si vas a usar el drawer para rutas tipo "chat/..."
                    navController.navigate(route)
                }
            )
        }

        composable(route = Routes.PANEL_FROM_REGISTER) {
            PanelScreen(
                onNavigateToMembers = { navController.navigate(Routes.MEMBERS) },
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL,
                        Routes.PANEL_FROM_LOGIN,
                        Routes.PANEL_FROM_REGISTER -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                },

                initialNotification = PanelNotificationType.REGISTER,
                onOpenAddTask = {               // <--- NUEVO
                    navController.navigate(Routes.ADD_TASK)
                },
                onOpenAssistantChat = {         // <--- NUEVO
                    navController.navigate(Routes.ASSISTANT_CHAT)
                },
                onTaskSelected = {
                    navController.navigate(Routes.TASK_DETAILS)
                },
                onNavigateToChat = { route ->
                    // si vas a usar el drawer para rutas tipo "chat/..."
                    navController.navigate(route)
                }
            )
        }

        // ---------- MEMBERS ----------
        composable(route = Routes.MEMBERS) {
            MembersScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                        Routes.MEMBERS -> { /* ya estás aquí */ }
                    }
                }
            )
        }

        // ---------- TASKS / CALENDAR / PROFILE ----------
        // ---------- TASKS ----------
        composable(route = Routes.TASKS) {
            TasksScreen(
                onTaskSelected = { task: HomeTask ->
                    navController.navigate(Routes.TASK_DETAILS)
                },
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> {
                            navController.navigate(Routes.TASKS) {
                                popUpTo(Routes.TASKS) { inclusive = true }
                            }
                        }
                        Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                },
                onAddTaskClick = {
                    navController.navigate(Routes.ADD_TASK)
                }
            )
        }

        // ---------- CREAR / AÑADIR TAREA ----------
        composable(route = Routes.ADD_TASK) {
            AddTaskScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                }
            )
        }

        // ---------- CHAT ASISTENTE IA ----------
        composable(route = Routes.ASSISTANT_CHAT) {
            AssistantChatScreen(
                onBackClick = { navController.popBackStack() }
            )
        }




        /*composable(route = Routes.CALENDAR) {
            SimplePlaceholderScreen(
                title = "Calendario",
                currentRoute = Routes.CALENDAR,
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> { }
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                }
            )
        }*/

        composable(route = Routes.PROFILE) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ---------- TASK DETAILS ----------
        composable(route = Routes.TASK_DETAILS) {
            TaskDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onOpenChatForAssignee = { memberId ->
                    navController.navigate(Routes.chat(memberId))
                },
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                }
            )
        }

            // ---------- CHAT ----------
        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("memberId") { type = NavType.IntType })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getInt("memberId") ?: 1
            val member = MockMembersRepository.getMemberById(memberId)
                ?: MockMembersRepository.members.first()

            ChatScreen(
                member = member,
                onBackClick = { navController.popBackStack() },
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                }
            )
        }

        composable(route = Routes.CALENDAR) {
            CalendarScreen(
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> { /* ya estás aquí */ }
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                },
                onOpenEventDetails = {
                    navController.navigate(Routes.EVENT_DETAILS)
                }
            )
        }

        composable(route = Routes.EVENT_DETAILS) {
            EventDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateBottom = { route ->
                    when (route) {
                        Routes.PANEL -> navController.navigate(Routes.PANEL)
                        Routes.TASKS -> navController.navigate(Routes.TASKS)
                        Routes.CALENDAR -> navController.navigate(Routes.CALENDAR)
                        Routes.MEMBERS -> navController.navigate(Routes.MEMBERS)
                    }
                }
            )
        }





    }
}*/

