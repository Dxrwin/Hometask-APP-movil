package com.iub.hometask.data.mock

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Member(
    val id: Int,
    val name: String,
    val role: String,      // "Adulto", "Niño", etc.
    val username: String,  // @usuario
    val isOnline: Boolean,
    val avatarUrl: String? = null // TODO: aquí podrías guardar URL o id de drawable
)

object MockMembersRepository {

    // Lista central reutilizable en todas las pantallas
    val members: SnapshotStateList<Member> = mutableStateListOf(
        Member(1, "Juan Pérez", "Adulto", "juanperez", isOnline = true),
        Member(2, "Sara Gómez", "Adulto", "sara", isOnline = true),
        Member(3, "Miguelito", "Niño", "miguelito", isOnline = false),
        Member(4, "Ana García", "Niña", "anagarcia", isOnline = true)
    )

    fun getMemberById(id: Int): Member? = members.firstOrNull { it.id == id }
}
