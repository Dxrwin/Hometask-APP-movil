package com.iub.hometask.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

// -------------------------------------------------------------
//  TOP BAR DEL CHAT
// -------------------------------------------------------------

@Composable
fun ChatTopBar(
    memberName: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // BackButton está en este mismo package (TopBars / NavigationComponents)
        BackButton(onClick = onBackClick)

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = memberName,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "En línea", // TODO: puedes usar un flag de member.isOnline
                color = PrimaryBlue,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "⋯",
            color = TextPrimary,
            fontSize = 20.sp
        )
    }
}

// -------------------------------------------------------------
//  MODELO DE MENSAJE DE CHAT
// -------------------------------------------------------------

data class ChatMessageUi(
    val id: Int,
    val author: String,
    val text: String,
    val time: String,
    val isMe: Boolean,
    val imageUri: Uri? = null
)

// -------------------------------------------------------------
//  LISTA Y BURBUJA DE MENSAJES
// -------------------------------------------------------------

@Composable
fun ChatMessageList(
    messages: List<ChatMessageUi>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messages) { msg ->
            ChatBubble(message = msg)
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessageUi
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isMe) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(CardDark.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                // TODO: avatar real del miembro
                Text(
                    text = message.author.firstOrNull()?.uppercase() ?: "",
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .background(
                    color = if (message.isMe) PrimaryBlue else CardDark,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (message.isMe) 18.dp else 4.dp,
                        bottomEnd = if (message.isMe) 4.dp else 18.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (!message.isMe) {
                Text(
                    text = message.author,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(
                text = message.text,
                color = TextPrimary,
                fontSize = 13.sp
            )

            if (message.imageUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDark.copy(alpha = 0.9f))
                ) {
                    // TODO: cargar imagen desde message.imageUri
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.time,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

// -------------------------------------------------------------
//  INPUT BAR DEL CHAT
// -------------------------------------------------------------

@Composable
fun ChatInputBar(
    textState: MutableState<String>,
    onAttachClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "+",
            color = TextSecondary,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable { onAttachClick() }
        )

        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Escribe un mensaje...",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            maxLines = 4,
            colors = TextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryBlue,
                focusedContainerColor = CardDark,
                unfocusedContainerColor = CardDark,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryBlue)
                .clickable { onSendClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "➤", color = TextPrimary, fontSize = 16.sp)
        }
    }
}
