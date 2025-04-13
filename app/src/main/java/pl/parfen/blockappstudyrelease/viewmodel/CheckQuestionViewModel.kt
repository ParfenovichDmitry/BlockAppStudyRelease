package pl.parfen.blockappstudyrelease.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.repository.PasswordRepository

data class CheckQuestionUiState(
    val selectedQuestion: String? = null,
    val answer: String = "",
    val errorMessage: String? = null
)

class CheckQuestionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CheckQuestionUiState())
    val uiState: StateFlow<CheckQuestionUiState> = _uiState

    fun selectQuestion(question: String) {
        _uiState.update { it.copy(selectedQuestion = question, errorMessage = null) }
    }

    fun updateAnswer(answer: String) {
        _uiState.update { it.copy(answer = answer, errorMessage = null) }
    }

    fun validateAndSave(context: Context, onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.selectedQuestion.isNullOrEmpty() || state.answer.isEmpty()) {
            _uiState.update { it.copy(errorMessage = context.getString(R.string.error_empty_fields)) }
        } else {
            PasswordRepository.saveSecretQuestion(context, state.selectedQuestion)
            PasswordRepository.saveSecretAnswer(context, state.answer)
            onSuccess()
        }
    }

    fun clear() {
        _uiState.value = CheckQuestionUiState()
    }
}
