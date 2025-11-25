package com.iub.hometask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.data.mock.Member
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

// ---------------- TOP BAR ----------------

@Composable
fun AddTaskTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(onClick = onBackClick)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Añadir Nueva Tarea",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ---------------- CAMPOS DE TEXTO ----------------

@Composable
fun TaskTitleField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Título de la Tarea",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = {
                Text(
                    text = "Ej. Limpiar la cocina",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryBlue,
                focusedContainerColor = CardDark,
                unfocusedContainerColor = CardDark,
                disabledContainerColor = CardDark,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun TaskDescriptionField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Descripción",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            placeholder = {
                Text(
                    text = "Añade más detalles sobre la tarea...",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            maxLines = 5,
            colors = TextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryBlue,
                focusedContainerColor = CardDark,
                unfocusedContainerColor = CardDark,
                disabledContainerColor = CardDark,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

// ---------------- FECHA DE VENCIMIENTO ----------------

@Composable
fun TaskDueDateField(
    formattedDate: String?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Fecha de Vencimiento",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedDate ?: "mm/dd/yyyy",
                color = if (formattedDate == null) TextSecondary else TextPrimary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            // TODO: reemplazar por icono de calendario real
            Text(text = "📅", color = TextSecondary, fontSize = 18.sp)
        }
    }
}

// ---------------- ASIGNAR MIEMBROS ----------------

@Composable
fun AssignMembersSection(
    members: List<Member>,
    selectedMemberIds: List<Int>,
    onToggleMember: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Asignar a Miembros",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // mostramos en filas de 2 elementos
            members.chunked(2).forEach { rowMembers ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowMembers.forEach { member ->
                        val selected = selectedMemberIds.contains(member.id)
                        MemberSelectableCard(
                            member = member,
                            selected = selected,
                            onClick = { onToggleMember(member.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // si la fila tiene solo 1, agregamos un Spacer para alinear
                    if (rowMembers.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Card “Otro”
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MemberOtherCard(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // TODO: abrir flujo para añadir nuevo miembro
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MemberSelectableCard(
    member: Member,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor =
        if (selected) PrimaryBlue else BackgroundDark.copy(alpha = 0.9f)
    val textColor =
        if (selected) TextPrimary else TextPrimary

    Row(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CardDark.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            // TODO: reemplazar por Image con avatar real
            Text(
                text = member.name.firstOrNull()?.uppercase() ?: "",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = member.name,
            color = textColor,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MemberOtherCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundDark.copy(alpha = 0.9f))
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CardDark.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+", color = TextPrimary, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Otro",
            color = TextPrimary,
            fontSize = 14.sp
        )
    }
}

// ---------------- BOTONES INFERIORES ----------------

@Composable
fun TaskFormButtons(
    onCancel: () -> Unit,
    onCreate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .clickable { onCancel() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cancelar",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PrimaryBlue)
                .clickable { onCreate() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Crear Tarea",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
