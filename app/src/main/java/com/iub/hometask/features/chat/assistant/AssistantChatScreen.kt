package com.iub.hometask.features.chat.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iub.hometask.data.mock.MockReplies
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AssistantChatScreen(
    onBackClick: () -> Unit
) {
    val messages = remember {
        mutableStateListOf(
            AssistantMessageUi(
                id = 1,
                isAssistant = true,
                text = "¡Hola! Soy tu asistente de gestión del hogar. Estoy aquí para ayudarte a organizar tareas, coordinar con miembros y mucho más. ¿En qué puedo ayudarte hoy?"
            ),
            AssistantMessageUi(
                id = 2,
                isAssistant = false,
                text = "¿Cómo puedo organizar las tareas semanales?"
            ),
            AssistantMessageUi(
                id = 3,
                isAssistant = true,
                text = "¡Buena pregunta! Puedes crear una lista de tareas recurrentes. Aquí tienes algunas sugerencias para empezar:\n\n• Crear una tarea para \"Limpieza semanal\".\n• Asignar \"Sacar la basura\" cada 3 días.\n• Programar \"Compra de víveres\" para los sábados."
            )
        )
    }

    val suggestionChips = listOf(
        "Crear tarea de limpieza",
        "Ver mi calendario",
        "Ver tareas de hoy",
        "Crear una nueva tarea",
        "Asignar a un miembro"
    )

    val inputText = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            AssistantTopBar(onBackClick = onBackClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
        ) {
            // Lista de mensajes
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AssistantMessagesList(
                    messages = messages,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp)
                )
            }

            // Chips de sugerencias
            AssistantSuggestionChipsRow(
                suggestions = suggestionChips,
                onSuggestionClick = { text ->
                    inputText.value = text
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            AssistantInputBar(
                textState = inputText,
                onSendClick = {
                    val text = inputText.value.trim()
                    if (text.isNotEmpty()) {
                        val myId = (messages.maxOfOrNull { it.id } ?: 0) + 1
                        messages.add(
                            AssistantMessageUi(
                                id = myId,
                                isAssistant = false,
                                text = text
                            )
                        )
                        inputText.value = ""

                        // Respuesta automática sencilla
                        scope.launch {
                            delay(1000)
                            val replyId = (messages.maxOfOrNull { it.id } ?: 0) + 1
                            messages.add(
                                AssistantMessageUi(
                                    id = replyId,
                                    isAssistant = true,
                                    text = MockReplies.randomChatReply()
                                )
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
