package pl.parfen.blockappstudyrelease.data.repository.blockapp

import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTManager

class AiTextRepositoryImpl(
    private val chatGPTManager: ChatGPTManager
) {
    fun getText(
        prompt: String,
        model: String = "gpt-3.5-turbo",
        callback: (String) -> Unit
    ) {
        chatGPTManager.getChatGPTResponse(prompt, model, false, callback)
    }
}
