package com.iub.hometask.features.calendar

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.data.mock.MockEventsRepository
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.TextPrimary

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EventDetailsScreen(
    eventId: Int,
    onBackClick: () -> Unit,
    //para Animación Hero
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    // 1. Obtener datos dinámicos
    val event = MockEventsRepository.getEventById(eventId)

    // Si no existe  mostrar placeholder o volver
    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Evento no encontrado", color = TextPrimary)
        }
        return
    }

    val participants = event.participantIds.mapNotNull { MockMembersRepository.getMemberById(it) }

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
                Text("Detalles del Evento", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Text("⋮", color = TextPrimary, fontSize = 20.sp)
            }
        }
    ) { innerPadding ->
        with(sharedTransitionScope) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 2. Info Card con Transición Hero
                Box(
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "event-${event.id}"),
                        animatedVisibilityScope
                    )
                ) {
                    EventInfoCard(
                        title = event.title,
                        description = event.description,
                        dateLabel = "${event.dateLabel} - ${event.timeLabel}"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Participantes
                Text("Participantes", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                ParticipantsPile(
                    participants = participants,
                    onAddClick = { /* Lógica añadir */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Tareas asociadas (Checkboxes simples)
                Text("Tareas rápidas", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                // añadir una lista simple de checkboxes si el evento tiene tareas
                // Por ahora un placeholder visual
                SatisfyingCheckboxItem(checked = false, onCheckedChange = {}, label = "Traer bebidas")
                SatisfyingCheckboxItem(checked = true, onCheckedChange = {}, label = "Confirmar asistencia")
            }
        }
    }
}