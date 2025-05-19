package pl.parfen.blockappstudyrelease.data.remote

import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTRequest
import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTResponse
import retrofit2.Response


interface ChatGPTRepository {
    suspend fun getChatResponse(request: ChatGPTRequest): Response<ChatGPTResponse>
}
