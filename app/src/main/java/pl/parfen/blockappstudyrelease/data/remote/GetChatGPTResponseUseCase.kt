package pl.parfen.blockappstudyrelease.data.remote

import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTRequest
import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTResponse
import retrofit2.Response


class GetChatGPTResponseUseCase(
    private val repository: ChatGPTRepository
) {
    suspend operator fun invoke(request: ChatGPTRequest): Response<ChatGPTResponse> {
        return repository.getChatResponse(request)
    }
}
