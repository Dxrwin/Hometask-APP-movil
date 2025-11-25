package com.iub.hometask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.data.mock.HomeTask
import com.iub.hometask.data.mock.Member
import com.iub.hometask.data.mock.TaskCategory
import com.iub.hometask.data.mock.TaskStatus
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

// -------------------------------------------------------------
//  TOP BAR "Tareas del Hogar"
// -------------------------------------------------------------

@Composable
fun TasksTopBar(
    onAddTaskClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // No back aquí, porque es pantalla principal con bottom nav.
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Tareas del Hogar",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "+", // TODO: reemplazar por icono real de añadir
            color = TextPrimary,
            fontSize = 24.sp,
            modifier = Modifier.clickable { onAddTaskClick() }
        )
    }
}

// -------------------------------------------------------------
//  FILTROS DE CATEGORÍA
// -------------------------------------------------------------

@Composable
fun TaskCategoryFilterRow(
    selectedCategory: TaskCategory?,              // null = Todas
    onCategorySelected: (TaskCategory?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskCategoryChip(
            text = "Todas",
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) }
        )
        TaskCategoryChip(
            text = "Cocina",
            selected = selectedCategory == TaskCategory.COCINA,
            onClick = { onCategorySelected(TaskCategory.COCINA) }
        )
        TaskCategoryChip(
            text = "Limpieza",
            selected = selectedCategory == TaskCategory.LIMPIEZA,
            onClick = { onCategorySelected(TaskCategory.LIMPIEZA) }
        )
        TaskCategoryChip(
            text = "Jardín",
            selected = selectedCategory == TaskCategory.JARDIN,
            onClick = { onCategorySelected(TaskCategory.JARDIN) }
        )
    }
}

@Composable
private fun TaskCategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (selected) PrimaryBlue else CardDark.copy(alpha = 0.9f)
    val textColor =
        if (selected) TextPrimary else TextSecondary

    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// -------------------------------------------------------------
//  SECCIÓN DE LISTA (Pendientes / Completadas)
// -------------------------------------------------------------

@Composable
fun TasksSectionCard(
    title: String,
    tasks: List<HomeTask>,
    members: List<Member>,
    onTaskClick: (HomeTask) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(20.dp))
                .padding(vertical = 8.dp)
        ) {
            if (tasks.isEmpty()) {
                Text(
                    text = "Sin tareas en esta categoría",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            } else {
                tasks.forEachIndexed { index, task ->
                    val assignedMembers =
                        members.filter { task.assignedMemberIds.contains(it.id) }

                    TaskListItem(
                        task = task,
                        assignedMembers = assignedMembers,
                        onClick = { onTaskClick(task) }
                    )

                    if (index != tasks.lastIndex) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
//  ITEM DE TAREA EN LA LISTA
// -------------------------------------------------------------

@Composable
fun TaskListItem(
    task: HomeTask,
    assignedMembers: List<Member>,
    onClick: () -> Unit
) {
    val isCompleted = task.status == TaskStatus.DONE
    val titleColor =
        if (isCompleted) TextSecondary.copy(alpha = 0.7f) else TextPrimary
    val subtitleColor =
        if (isCompleted) TextSecondary.copy(alpha = 0.6f) else TextSecondary
    val iconText = if (isCompleted) "✓" else "○"
    val iconColor =
        if (isCompleted) PrimaryBlue else TextSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(CardDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iconText,
                color = iconColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = task.title,
                color = titleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = task.dueLabel,
                color = subtitleColor,
                fontSize = 12.sp
            )
        }

        // Avatares pequeños (iniciales) de miembros asignados
        Row(
            horizontalArrangement = Arrangement.spacedBy(-8.dp)
        ) {
            assignedMembers.take(3).forEach { member ->
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(CardDark.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: sustituir por Image con avatar real
                    Text(
                        text = member.name.firstOrNull()?.uppercase() ?: "",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
