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
import com.iub.hometask.data.mock.Member
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun MembersTopBar(
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
            text = "Miembros del Hogar",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "⋯",
            color = TextPrimary,
            fontSize = 20.sp,
            modifier = Modifier.clickable { /* TODO: menú extra */ }
        )
    }
}

@Composable
fun MemberListItem(
    member: Member,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardDark.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Cargar aquí la imagen del miembro con Image(...)
            Text(
                text = member.name.firstOrNull()?.uppercase() ?: "",
                color = TextPrimary,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = member.name,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = member.role,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Text(
            text = "✎",
            color = TextSecondary,
            fontSize = 18.sp,
            modifier = Modifier.clickable { onEditClick() }
        )
    }
}
