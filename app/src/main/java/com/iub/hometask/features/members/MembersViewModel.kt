package com.iub.hometask.features.members

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.iub.hometask.data.remote.RetrofitClient
import com.iub.hometask.data.repository.MemberRepository
import com.iub.hometask.data.repository.MemberUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MembersUiState {
    object Loading : MembersUiState()
    data class Success(val members: List<MemberUiModel>) : MembersUiState()
    data class Error(val message: String) : MembersUiState()
}

class MembersViewModel(private val repository: MemberRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MembersUiState>(MembersUiState.Loading)
    val uiState: StateFlow<MembersUiState> = _uiState

    init {
        loadMembers()
    }

    fun loadMembers() {
        viewModelScope.launch {
            _uiState.value = MembersUiState.Loading
            repository.getMembers().fold(
                onSuccess = { members ->
                    _uiState.value = MembersUiState.Success(members)
                },
                onFailure = { error ->
                    _uiState.value = MembersUiState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                val context = app.applicationContext
                val api = RetrofitClient.getMemberService(context)
                val repo = MemberRepository(api)
                MembersViewModel(repo)
            }
        }
    }
}