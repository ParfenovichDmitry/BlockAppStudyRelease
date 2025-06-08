package pl.parfen.blockappstudyrelease.utils.blockapp

import kotlinx.coroutines.suspendCancellableCoroutine
import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTManager
import kotlin.coroutines.resume

class GetAiTextUseCase(
    private val chatGPTManager: ChatGPTManager
) {
    suspend operator fun invoke(prompt: String): String {
        return suspendCancellableCoroutine { continuation ->
            chatGPTManager.getChatGPTResponse(
                prompt = prompt,
                model = "gpt-3.5-turbo",
                store = false
            ) { result ->
                continuation.resume(result)
            }
        }
    }
}
