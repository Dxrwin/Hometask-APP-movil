package com.iub.hometask.data.remote.api

import com.iub.hometask.data.dto.member.MemberResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface MemberApiService {
    @GET("miembros/todos")
    suspend fun getAllMembers(): Response<List<MemberResponseDto>>
}