package com.iub.hometask.features.tasks.details

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.data.mock.MockReplies
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.components.* // Aquí debe estar ActionPlanTimeline y ActionStep
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TaskDetailsScreen(
    taskId: Int,
    onBackClick: () -> Unit,
    onOpenChatForAssignee: (Int) -> Unit = {},
    onNavigateBottom: (String) -> Unit = {},
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    val currentRoute = Routes.TASKS
    val scope = rememberCoroutineScope()

    // Datos Mock específicos para la vista de Tarea (Timeline)
    val actionSteps = remember {
        listOf(
            ActionStep("Organizar la despensa", true, StepStatus.IN_PROGRESS),
            ActionStep("Limpiar superficies y microondas", false, StepStatus.PENDING),
            ActionStep("Fregar el suelo", false, StepStatus.PENDING)
        )
    }

    // Asignado a (Mock)
    val assignee = MockMembersRepository.getMemberById(1) // Juan Pérez

    // Comentarios
    val commentText = remember { mutableStateOf("") }
    val attachedUri = remember { mutableStateOf<Uri?>(null) }
    val comments = remember {
        mutableStateListOf(
            CommentUi(1, "Sara", "¡No te olvides de usar el producto nuevo!", "Hace 5 minutos"),
            CommentUi(2, "Juan Pérez", "Entendido. Así quedó la despensa.", "Hace 2 minutos")
        )
    }

    val attachLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> attachedUri.value = uri }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = onBackClick)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Detalles de Tarea",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "⋮", color = TextPrimary, fontSize = 20.sp)
            }
        },
        bottomBar = {
            HomeBottomNavigationBar(currentRoute = currentRoute, onItemSelected = onNavigateBottom)
        }
    ) { innerPadding ->
        with(sharedTransitionScope) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 1. HEADER ESPECÍFICO DE TAREA (Con Hero Transition)
                // Usamos la clave "task-$taskId"
                Box(
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(key = "task-$taskId"),
                            animatedVisibilityScope
                        )
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Limpiar la cocina a fondo",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Asegurarse de limpiar todas las superficies, fregar el suelo, limpiar el microondas por dentro y organizar la despensa.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Fila de Fechas
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Creada", color = TextSecondary, fontSize = 12.sp)
                                Text("15 de Jul, 2024", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Fecha Límite", color = TextSecondary, fontSize = 12.sp)
                                Text("Hoy, 22:00", color = Color(0xFFFF9800), fontSize = 14.sp, fontWeight = FontWeight.Medium) // Naranja
                            }
                        }
                    }
                }

                // 2. PLAN DE ACCIÓN (TIMELINE) - Componente Externo
                ActionPlanTimeline(steps = actionSteps)

                // 3. ASIGNADA A (Tarjeta de usuario único)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("Asignada a", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Gray, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(assignee?.name?.take(1) ?: "J", color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(assignee?.name ?: "Juan Pérez", color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text("@${assignee?.username ?: "juanperez"}", color = TextSecondary, fontSize = 12.sp)
                        }

                        // Botones de acción rápida
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(BackgroundDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PrimaryBlue.copy(alpha=0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💬", fontSize = 14.sp)
                        }
                    }
                }

                // 4. COMENTARIOS
                AccordionCard(title = "Comentarios", initiallyExpanded = true) {
                    CommentsList(comments = comments)
                    Spacer(modifier = Modifier.height(12.dp))
                    CommentInputBar(
                        textState = commentText,
                        onAttachClick = { attachLauncher.launch("image/*") },
                        onSendClick = {
                            if (commentText.value.isNotBlank() || attachedUri.value != null) {
                                comments.add(CommentUi(comments.size + 1, "Tú", commentText.value.ifBlank { "Imagen" }, "Ahora", attachedUri.value))
                                commentText.value = ""
                                attachedUri.value = null
                                scope.launch {
                                    delay(1000)
                                    comments.add(CommentUi(comments.size + 1, "Juan", MockReplies.randomTaskReply(), "Ahora"))
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}