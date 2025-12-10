package com.iub.hometask.features.chat

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.iub.hometask.data.local.SessionManager
import com.iub.hometask.data.remote.RetrofitClient
import com.iub.hometask.data.repository.ChatRepository
import com.iub.hometask.data.repository.ChatMessage
import com.iub.hometask.utils.UriUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ChatUiState {
    object Loading : ChatUiState()
    data class Success(val messages: List<ChatMessage>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

data class ChatHeaderState(
    val title: String = "Cargando...",
    val imageUrl: String? = null,
    val isLoading: Boolean = true
)

class ChatViewModel(
    private val repository: ChatRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Estado de mensajes
    private val _messagesState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val messagesState: StateFlow<ChatUiState> = _messagesState

    // Estado del encabezado (Usuario con quien hablo)
    private val _headerState = MutableStateFlow(ChatHeaderState())
    val headerState: StateFlow<ChatHeaderState> = _headerState

    private var currentRecipientId: Int = 0

    fun loadChatData(recipientId: Int) {
        currentRecipientId = recipientId
        viewModelScope.launch {
            // 1. Cargar Header (Nombre y Foto real)
            repository.getMemberDetails(recipientId).fold(
                onSuccess = { member ->
                    _headerState.value = ChatHeaderState(title = member.name, imageUrl = member.imageUrl, isLoading = false)
                },
                onFailure = {
                    _headerState.value = ChatHeaderState(title = "Usuario $recipientId", isLoading = false)
                }
            )

            // 2. Cargar Mensajes
            loadMessages()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            repository.getMessages(currentRecipientId).fold(
                onSuccess = { msgs -> _messagesState.value = ChatUiState.Success(msgs) },
                onFailure = { err -> _messagesState.value = ChatUiState.Error(err.message ?: "Error") }
            )
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        uploadAndSendMessage(content, null)
    }

    // Función para manejar imagen + texto
    fun uploadAndSendImage(uri: Uri, context: Context) {
        val file = UriUtils.getFileFromUri(context, uri)
        if (file != null) {
            viewModelScope.launch {
                // Primero subimos la imagen
                repository.uploadImage(file).fold(
                    onSuccess = { relativePath ->
                        // Luego enviamos el mensaje con la ruta
                        uploadAndSendMessage("Imagen adjunta", relativePath)
                    },
                    onFailure = { /* Manejar error de subida */ }
                )
            }
        }
    }

    private fun uploadAndSendMessage(content: String, imageUrl: String?) {
        viewModelScope.launch {
            val hogarId = sessionManager.getHogarId() // ¡Importante para el 403!

            if (hogarId == -1) {
                _messagesState.value = ChatUiState.Error("Error de sesión: No se encontró el Hogar")
                return@launch
            }

            val result = repository.sendMessage(currentRecipientId, content, hogarId, imageUrl)
            if (result.isSuccess) {
                loadMessages() // Recargar al enviar
            } else {
                // Manejar error de envío
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                val context = app.applicationContext
                val api = RetrofitClient.getChatService(context)
                val session = SessionManager(context)
                val repo = ChatRepository(api, session)
                ChatViewModel(repo, session)
            }
        }
    }
}