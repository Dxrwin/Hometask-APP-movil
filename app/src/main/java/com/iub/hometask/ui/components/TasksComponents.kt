package com.iub.hometask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.data.mock.HomeTask
import com.iub.hometask.data.mock.Member
import com.iub.hometask.data.mock.TaskStatus
import com.iub.hometask.ui.theme.*

// --- 1. TOP BAR ---
@Composable
fun TasksTopBar(onAddTaskClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(24.dp)) // Icono notif placeholder
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Tareas del Hogar",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "+",
            color = TextPrimary,
            fontSize = 30.sp,
            modifier = Modifier.clickable { onAddTaskClick() }
        )
    }
}

// --- 2. CLASIFICACIÓN DE TAREAS (Tarjetas Rojas/Amarillas) ---
@Composable
fun TaskClassificationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(CardDark, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Clasificación de Tareas", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Ver todo", color = PrimaryBlue, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta Prioritarias
        TaskStatRow(
            icon = "!",
            iconBg = Color(0xFFEF5350).copy(alpha = 0.2f), // Rojo suave
            iconColor = Color(0xFFEF5350),
            title = "Prioritarias",
            subtitle = "3 tareas"
        )
        Spacer(modifier = Modifier.height(12.dp))
        // Tarjeta Próximas
        TaskStatRow(
            icon = "⏳",
            iconBg = Color(0xFFFFCA28).copy(alpha = 0.2f), // Amarillo suave
            iconColor = Color(0xFFFFCA28),
            title = "Próximas a Vencer",
            subtitle = "5 tareas"
        )
    }
}

@Composable
fun TaskStatRow(icon: String, iconBg: Color, iconColor: Color, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 24.sp, color = iconColor)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
        }
        Text(">", color = TextSecondary, fontSize = 14.sp)
    }
}

// --- 3. CARRUSEL DE MIEMBROS (Con Badge Azul) ---
@Composable
fun MembersCarousel(members: List<Member>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text("Miembros con Pendientes", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(members) { member ->
                MemberAvatarItem(member)
            }
        }
    }
}

@Composable
fun MemberAvatarItem(member: Member) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Gray) // Placeholder imagen
                    .border(2.dp, CardDark, CircleShape)
            )

            // Badge con número
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(PrimaryBlue, CircleShape)
                    .border(2.dp, BackgroundDark, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${member.id}", // Simulación de tareas pendientes
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(member.name.split(" ")[0], color = TextSecondary, fontSize = 12.sp)
    }
}

// --- 4. LISTA DE TAREAS MODERNA ---
@Composable
fun TaskListSection(
    title: String,
    tasks: List<HomeTask>,
    members: List<Member>, // Para mostrar el avatar asignado
    onTaskClick: (HomeTask) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(24.dp))
                .padding(vertical = 8.dp)
        ) {
            tasks.forEach { task ->
                val assignedMember = members.firstOrNull { it.id == task.assignedMemberIds.firstOrNull() }
                TaskItemModern(task, assignedMember, onTaskClick)
            }
        }
    }
}

@Composable
fun TaskItemModern(
    task: HomeTask,
    assignedMember: Member?,
    onClick: (HomeTask) -> Unit
) {
    val isCompleted = task.status == TaskStatus.DONE

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(task) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Radio Button Grande
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(2.dp, if(isCompleted) PrimaryBlue else TextSecondary.copy(alpha=0.5f), CircleShape)
                .background(if(isCompleted) PrimaryBlue else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if(isCompleted) Text("✓", color = TextPrimary, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Textos
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                color = if(isCompleted) TextSecondary else TextPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                style = if(isCompleted) androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else androidx.compose.ui.text.TextStyle()
            )
            Spacer(modifier = Modifier.height(4.dp))
            val statusText = if (isCompleted) "Completada: Ayer" else "Vence: ${task.dueLabel}"
            val statusColor = if (task.category.name == "JARDIN") Color(0xFFFF9800) else TextSecondary // Ejemplo de color naranja para fechas límite
            Text(text = statusText, color = if(isCompleted) TextSecondary else statusColor, fontSize = 12.sp)
        }

        // Avatar Asignado (Mini)
        if (assignedMember != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Gray) // Placeholder imagen
            )
        }
    }
}