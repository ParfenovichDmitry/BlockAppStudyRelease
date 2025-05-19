package pl.parfen.blockappstudyrelease.data.repository

import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTRequest
import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTResponse
import pl.parfen.blockappstudyrelease.data.remote.ChatGPTRepository
import pl.parfen.blockappstudyrelease.data.remote.ChatGPTService
import retrofit2.Response


class ChatGPTRepositoryImpl(
    private val service: ChatGPTService,
    private val apiKey: String
) : ChatGPTRepository {

    override suspend fun getChatResponse(request: ChatGPTRequest): Response<ChatGPTResponse> {
        return service.createCompletion("Bearer $apiKey", request)
    }
}
