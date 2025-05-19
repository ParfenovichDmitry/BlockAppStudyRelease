package pl.parfen.blockappstudyrelease.data.remote


import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTRequest
import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatGPTService {
    @POST("v1/chat/completions")
    suspend fun createCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatGPTRequest
    ): Response<ChatGPTResponse>
}
