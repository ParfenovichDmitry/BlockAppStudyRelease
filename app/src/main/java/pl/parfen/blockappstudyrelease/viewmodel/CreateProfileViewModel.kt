package pl.parfen.blockappstudyrelease.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.data.local.db.AppDatabase
import pl.parfen.blockappstudyrelease.data.preferences.ProfilePreferences


class CreateProfileViewModel(
    @SuppressLint("StaticFieldLeak") private val context: Context
) : ViewModel() {

    private val db = AppDatabase.getDatabase(context)
    private val _uiState = MutableStateFlow(CreateProfileUiState())
    val uiState: StateFlow<CreateProfileUiState> = _uiState

    private lateinit var prefs: ProfilePreferences
    private var profileId: Int = -1

    fun initProfile(id: Int) {
        profileId = id
        prefs = ProfilePreferences(context, profileId)
        loadProfile()
        loadPreferences()
    }

    private fun loadProfile() {
        if (profileId != -1) {
            viewModelScope.launch(Dispatchers.IO) {
                val profile = db.profileDao().getProfileById(profileId)
                val bookProgress = db.bookProgressDao().getProgressForProfile(profileId)
                withContext(Dispatchers.Main) {
                    profile?.let {
                        _uiState.value = _uiState.value.copy(
                            avatarUri = it.avatar?.let(Uri::parse),
                            nickname = it.nickname,
                            age = it.age.toString(),
                            password = it.password.orEmpty(),
                            selectedResource = it.selectedResource,
                            usageTime = it.usageTime.toString(),
                            percentage = it.percentage.toString(),
                            blockedApps = it.blockedApps,
                            books = it.books,
                            activeBook = it.activeBook,
                            aiNetwork = it.aiNetwork,
                            aiTopics = it.aiTopics,
                            aiLanguage = it.aiLanguage,
                            showAllBooks = it.showAllBooks,
                            additionalLanguage = it.additionalLanguage,
                            selectedTopics = it.selectedTopics,
                            totalWordsRead = it.totalWordsRead,
                            profileLanguage = it.profileLanguage ?: HelpMethods.getSystemLanguage(),
                            bookProgress = bookProgress
                        )
                    }
                }
            }
        }
    }
    val currentProfileId: Int
        get() = profileId
    private fun loadPreferences() {
        _uiState.value = _uiState.value.copy(
            aiNetwork = prefs.aiNetwork,
            aiTopics = prefs.aiTopics,
            aiLanguage = prefs.aiLanguage,
            additionalLanguage = prefs.additionalLanguage,
            selectedTopics = prefs.selectedTopics
        )
    }

    fun updateNickname(nickname: String) {
        _uiState.value = _uiState.value.copy(nickname = nickname)
    }

    fun updateAge(age: String) {
        _uiState.value = _uiState.value.copy(age = age)
    }

    fun updateAvatar(uri: Uri?) {
        _uiState.value = _uiState.value.copy(avatarUri = uri)
    }

    fun updateSelectedResource(resource: String) {
        _uiState.value = _uiState.value.copy(selectedResource = resource)
    }

    fun updateUsageTime(usageTime: String) {
        val validated = usageTime.toIntOrNull()?.coerceIn(1, 120)?.toString() ?: "1"
        _uiState.value = _uiState.value.copy(usageTime = validated)
    }

    fun updatePercentage(percentage: String) {
        val validated = percentage.toIntOrNull()?.coerceIn(40, 100)?.toString() ?: "40"
        _uiState.value = _uiState.value.copy(percentage = validated)
    }

    fun updateBlockedApps(apps: List<String>) {
        _uiState.value = _uiState.value.copy(blockedApps = apps)
    }

    fun updateBooksAndActiveBook(books: List<String>, activeBook: String) {
        _uiState.value = _uiState.value.copy(books = books, activeBook = activeBook)
    }

    fun updateAI(aiNetwork: String, aiTopics: List<String>) {
        _uiState.value = _uiState.value.copy(aiNetwork = aiNetwork, aiTopics = aiTopics)
    }

    fun showAgeValidationAlert() {
        _uiState.value = _uiState.value.copy(showAgeValidationAlert = true)
    }

    fun dismissAgeValidationAlert() {
        _uiState.value = _uiState.value.copy(showAgeValidationAlert = false)
    }

    fun saveProfile(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value

        if (currentState.age.isBlank()) {
            onError("Введите возраст")
            return
        }

        if (!currentState.isAgeValid()) {
            onError("Возраст должен быть от 6 до 15 лет")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val profile = currentState.toProfile(profileId)
                val profileDao = db.profileDao()

                if (profileId == -1) {
                    profileDao.insert(profile)
                } else {
                    profileDao.update(profile)
                }

                prefs.clear()

                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Ошибка сохранения профиля")
                }
            }
        }
    }
}