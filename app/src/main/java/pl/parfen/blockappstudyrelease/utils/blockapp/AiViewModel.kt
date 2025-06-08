package pl.parfen.blockappstudyrelease.utils.blockapp


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class AiViewModel(
    private val getAiTextUseCase: GetAiTextUseCase
) : ViewModel() {
    var aiResponse: String = ""
        private set

    var isLoading: Boolean = false
        private set

    fun getAiResponse(prompt: String) {
        isLoading = true
        aiResponse = ""
        viewModelScope.launch {
            try {
                aiResponse = getAiTextUseCase(prompt)
            } catch (e: Exception) {
                aiResponse = "Ошибка: ${e.message}"
            }
            isLoading = false
        }
    }
}
