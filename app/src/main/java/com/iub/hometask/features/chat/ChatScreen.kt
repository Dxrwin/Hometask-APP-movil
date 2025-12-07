package com.iub.hometask.features.chat
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iub.hometask.data.mock.Member
import com.iub.hometask.data.mock.MockReplies
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun ChatScreen(
    member: Member,
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val currentRoute = Routes.MEMBERS
    val scope = rememberCoroutineScope()

    val attachUri = remember { mutableStateOf<Uri?>(null) }
    val messageText = remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            ChatMessageUi(
                id = 1,
                author = member.name,
                text = "¡Hola! ¿Podrías sacar la basura cuando llegues a casa?",
                time = "10:30 AM",
                isMe = false
            ),
            ChatMessageUi(
                id = 2,
                author = "Tú",
                text = "Claro, sin problema. Llego en unos 20 minutos.",
                time = "10:32 AM",
                isMe = true
            ),
            ChatMessageUi(
                id = 3,
                author = member.name,
                text = "Perfecto, ¡gracias!",
                time = "10:33 AM",
                isMe = false
            )
        )
    }

    val attachLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        attachUri.value = uri
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            ChatTopBar(
                memberName = member.name,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            HomeBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = onNavigateBottom
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            ChatMessageList(
                messages = messages,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ChatInputBar(
                textState = messageText,
                onAttachClick = { attachLauncher.launch("image/*") },
                onSendClick = {
                    val text = messageText.value.trim()
                    if (text.isNotEmpty() || attachUri.value != null) {
                        val myMessage = ChatMessageUi(
                            id = messages.size + 1,
                            author = "Tú",
                            text = if (text.isEmpty()) "(Imagen adjunta)" else text,
                            time = "Ahora",
                            isMe = true,
                            imageUri = attachUri.value
                        )
                        messages.add(myMessage)
                        messageText.value = ""
                        attachUri.value = null

                        scope.launch {
                            delay(1000)
                            val reply = ChatMessageUi(
                                id = messages.size + 1,
                                author = member.name,
                                text = MockReplies.randomChatReply(),
                                time = "Ahora",
                                isMe = false
                            )
                            messages.add(reply)
                        }
                    }
                }
            )
        }
    }
}
