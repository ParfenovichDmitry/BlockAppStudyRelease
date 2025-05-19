package pl.parfen.blockappstudyrelease.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTRequest
import pl.parfen.blockappstudyrelease.data.model.ai.Message
import pl.parfen.blockappstudyrelease.data.remote.GetChatGPTResponseUseCase
import pl.parfen.blockappstudyrelease.ui.ai.AIEvent
import pl.parfen.blockappstudyrelease.ui.ai.AIUiState
import pl.parfen.blockappstudyrelease.util.HelpMethods
import java.util.*

class AIViewModel(
    application: Application,
    private val getChatGPTResponseUseCase: GetChatGPTResponseUseCase
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext
    private val _uiState = MutableStateFlow(AIUiState())
    val uiState: StateFlow<AIUiState> = _uiState

    private val random = Random(System.currentTimeMillis())

    fun onEvent(event: AIEvent) {
        when (event) {
            is AIEvent.SubmitPrompt -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    responseText = ""
                )

                viewModelScope.launch {
                    try {
                        val targetLang = if (event.useOnlySecondLanguage && !event.additionalLanguage.isNullOrBlank()) {
                            event.additionalLanguage
                        } else {
                            Locale.getDefault().language
                        }

                        val topic = if (event.selectedTopics.isNotEmpty()) {
                            event.selectedTopics[random.nextInt(event.selectedTopics.size)]
                        } else {
                            val fallback = context.resources.getStringArray(
                                context.resources.getIdentifier("default_topics", "array", context.packageName)
                            )
                            fallback[random.nextInt(fallback.size)]
                        }

                        val prompt = HelpMethods.createPrompt(
                            context = context,
                            age = event.age,
                            topic = topic,
                            languageCode = targetLang,
                            languageCodes = listOf(Locale.getDefault().language, targetLang),
                            breakIntoSyllables = event.breakIntoSyllables
                        )

                        val request = ChatGPTRequest(
                            messages = listOf(Message("user", prompt)),
                            model = event.model,
                            store = event.store
                        )

                        val response = getChatGPTResponseUseCase(request)

                        if (response.isSuccessful) {
                            val content = response.body()?.choices?.firstOrNull()?.message?.content
                            _uiState.value = _uiState.value.copy(
                                responseText = content?.trim().orEmpty(),
                                isLoading = false
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = "Error: ${response.code()}",
                                isLoading = false
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = e.message ?: "Unknown error",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
