package pl.parfen.blockappstudyrelease.viewmodel.blockapp

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.domain.usecase.GetTextForReadingUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.ValidatePasswordUseCase
import pl.parfen.blockappstudyrelease.utils.blockapp.ComplianceCalculator

class BlockedAppViewModel(
    application: Application,
    private val blockedAppName: String,
    private val profileId: Int,
    private val fileUri: Uri?,
    private val getTextForReadingUseCase: GetTextForReadingUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val requiredCompliance: Float,
    private val selectedBookTitle: String? // новый параметр
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(BlockedAppUiState())
    val uiState: StateFlow<BlockedAppUiState> = _uiState

    init {
        _uiState.value = _uiState.value.copy(
            blockedAppName = blockedAppName,
            isLoading = true,
            isTextLoaded = false,
            textToRead = null
        )
        loadTextToRead()
    }

    private fun loadTextToRead() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val text = getTextForReadingUseCase(
                context = context,
                profileId = profileId,
                selectedBookTitle = selectedBookTitle // передаём явно
            )
            _uiState.value = _uiState.value.copy(
                textToRead = text,
                isLoading = false,
                isTextLoaded = text.isNotBlank()
            )
        }
    }

    fun startReading() {
        _uiState.value = _uiState.value.copy(isReading = true)
    }

    fun stopReading() {
        _uiState.value = _uiState.value.copy(isReading = false)
    }

    fun onSpeechRecognized(recognizedText: String) {
        val expectedText = _uiState.value.textToRead ?: return
        val compliance = ComplianceCalculator.calculateCompliance(recognizedText, expectedText)

        _uiState.value = _uiState.value.copy(progressPercent = compliance)

        if (compliance >= requiredCompliance) {
            unlockApp()
        }
    }

    private fun unlockApp() {
        _uiState.value = _uiState.value.copy(showPasswordDialog = false, passwordError = null)
        // Вызов коллбэка осуществляется извне через uiState
    }

    fun showPasswordDialog() {
        _uiState.value = _uiState.value.copy(showPasswordDialog = true)
    }

    fun cancelPasswordDialog() {
        _uiState.value = _uiState.value.copy(showPasswordDialog = false, passwordError = null)
    }

    fun checkPassword(password: String) {
        viewModelScope.launch {
            val isValid = validatePasswordUseCase(profileId, password)
            if (isValid) {
                unlockApp()
            } else {
                _uiState.value = _uiState.value.copy(passwordError = "Неверный пароль")
            }
        }
    }
}
