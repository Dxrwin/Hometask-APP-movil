package com.iub.hometask.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

// Modelo de datos para un comentario
data class CommentUi(
    val id: Int,
    val authorName: String,
    val text: String,
    val timeLabel: String,
    val imageUri: Uri? = null
)

@Composable
fun CommentsList(
    comments: List<CommentUi>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        comments.forEach { comment ->
            CommentItem(comment)
        }
    }
}

@Composable
fun CommentItem(comment: CommentUi) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.authorName.take(1),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = comment.authorName,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.timeLabel,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Burbuja de texto
            Box(
                modifier = Modifier
                    .background(CardDark.copy(alpha = 0.5f), RoundedCornerShape(0.dp, 12.dp, 12.dp, 12.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = comment.text,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            // Si hay imagen adjunta (simulada)
            if (comment.imageUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                        .border(1.dp, TextSecondary, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Imagen", color = Color.White, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun CommentInputBar(
    textState: MutableState<String>,
    onAttachClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDark, RoundedCornerShape(24.dp))
            .border(1.dp, CardDark, RoundedCornerShape(24.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            placeholder = { Text("Añade un comentario...", color = TextSecondary, fontSize = 13.sp) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        // Botón Adjuntar
        IconButton(onClick = onAttachClick) {
            Text("📎", fontSize = 18.sp)
        }

        // Botón Enviar
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PrimaryBlue, CircleShape)
                .clickable { onSendClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("➤", color = Color.White, fontSize = 16.sp) // O usa un Icono real
        }
    }
}