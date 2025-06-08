package pl.parfen.blockappstudyrelease.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.data.database.AppDatabase
import java.util.Locale

class CreateProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CreateProfileUiState())
    val uiState: StateFlow<CreateProfileUiState> = _uiState

    private val db = AppDatabase.getDatabase(application)

    var currentProfileId: Int = -1
        private set

    fun initProfile(profileId: Int, languageFromBaseActivity: String) {
        currentProfileId = profileId
        if (profileId != -1) {
            viewModelScope.launch {
                db.profileDao().getProfileById(profileId)?.let { profile ->
                    _uiState.update {
                        it.copy(
                            avatarUri = Uri.parse(profile.avatar),
                            nickname = profile.nickname,
                            age = profile.age.toString(),
                            password = profile.password ?: "",
                            selectedResource = profile.selectedResource,
                            usageTime = profile.usageTime.toString(),
                            percentage = profile.percentage.toString(),
                            blockedApps = profile.blockedApps,
                            books = profile.books,
                            activeBook = profile.activeBook,
                            aiNetwork = profile.aiNetwork,
                            aiTopics = profile.aiTopics,
                            aiLanguage = profile.aiLanguage,
                            showAllBooks = profile.showAllBooks,
                            additionalLanguage = profile.additionalLanguage,
                            profileLanguage = profile.profileLanguage,
                            selectedTopics = profile.selectedTopics,
                            totalWordsRead = profile.totalWordsRead
                        )
                    }
                }
            }
        } else {
            _uiState.update {
                it.copy(profileLanguage = languageFromBaseActivity)
            }
        }
    }



    fun updateAvatar(uri: Uri?) {
        _uiState.update { it.copy(avatarUri = uri) }
    }

    fun updateNickname(nickname: String) {
        _uiState.update { it.copy(nickname = nickname) }
    }

    fun updateAge(age: String) {
        _uiState.update { it.copy(age = age) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun updateSelectedResource(resource: String) {
        _uiState.update { it.copy(selectedResource = resource) }
    }

    fun updateUsageTime(time: String) {
        _uiState.update { it.copy(usageTime = time) }
    }

    fun updatePercentage(percentage: String) {
        _uiState.update { it.copy(percentage = percentage) }
    }

    fun updateBlockedApps(apps: List<String>) {
        _uiState.update { it.copy(blockedApps = apps) }
    }

    fun updateBooksAndActiveBook(books: List<String>, activeBook: String) {
        _uiState.update { it.copy(books = books, activeBook = activeBook) }
    }

    fun updateAiNetwork(network: String) {
        _uiState.update { it.copy(aiNetwork = network) }
    }

    fun updateAiTopics(topics: List<String>) {
        _uiState.update { it.copy(aiTopics = topics) }
    }

    fun updateAI(network: String, topics: List<String>) {
        _uiState.update { it.copy(aiNetwork = network, aiTopics = topics) }
    }

    fun updateAiLanguage(language: String) {
        _uiState.update { it.copy(aiLanguage = language) }
    }

    fun updateProfileLanguage(language: String) {
        _uiState.update { it.copy(profileLanguage = language) }
    }

    fun updateShowAllBooks(show: Boolean) {
        _uiState.update { it.copy(showAllBooks = show) }
    }

    fun updateAdditionalLanguage(language: String?) {
        _uiState.update { it.copy(additionalLanguage = language) }
    }

    fun updateSelectedTopics(topics: List<String>) {
        _uiState.update { it.copy(selectedTopics = topics) }
    }

    fun updateTotalWordsRead(words: Int) {
        _uiState.update { it.copy(totalWordsRead = words) }
    }

    fun saveProfile(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val currentUiState = _uiState.value
                val finalProfileLanguage = if (currentUiState.profileLanguage.isBlank()) {
                    Locale.getDefault().language.ifBlank { "pl" }
                } else {
                    currentUiState.profileLanguage
                }

                val profileToSave = currentUiState.toProfile(currentProfileId).copy(
                    profileLanguage = finalProfileLanguage
                )

                db.profileDao().insert(profileToSave)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Ошибка сохранения профиля")
            }
        }
    }

    fun validateAge(): Boolean {
        val isValid = _uiState.value.isAgeValid()
        _uiState.update { it.copy(showAgeValidationAlert = !isValid) }
        return isValid
    }

    fun showAgeValidationAlert() {
        _uiState.update { it.copy(showAgeValidationAlert = true) }
    }

    fun dismissAgeValidationAlert() {
        _uiState.update { it.copy(showAgeValidationAlert = false) }
    }
}
