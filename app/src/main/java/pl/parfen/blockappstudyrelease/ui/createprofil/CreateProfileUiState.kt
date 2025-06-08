package pl.parfen.blockappstudyrelease.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import pl.parfen.blockappstudyrelease.data.model.BookProgress
import pl.parfen.blockappstudyrelease.data.model.Profile

data class CreateProfileUiState(
    val avatarUri: Uri? = null,
    val nickname: String = "",
    val age: String = "",
    val password: String = "",
    val selectedResource: String = "book",
    val usageTime: String = "1",
    val percentage: String = "40",
    val blockedApps: List<String> = emptyList(),
    val books: List<String> = emptyList(),
    val activeBook: String = "",
    val aiNetwork: String = "ChatGPT",
    val aiTopics: List<String> = emptyList(),
    val aiLanguage: String = "English",
    val showAllBooks: Boolean = false,
    val additionalLanguage: String? = null,
    val selectedTopics: List<String> = emptyList(),
    val totalWordsRead: Int = 0,
    val profileLanguage: String = "en", // <- дефолт, но будет переопределён
    val bookProgress: List<BookProgress> = emptyList(),
    val showAgeValidationAlert: Boolean = false
) {
    fun toProfile(profileId: Int): Profile {
        return Profile(
            id = if (profileId != -1) profileId else 0,
            avatar = avatarUri?.toString() ?: "",
            nickname = nickname,
            age = age.toIntOrNull() ?: 0,
            password = password,
            selectedResource = selectedResource,
            usageTime = usageTime.toIntOrNull() ?: 1,
            percentage = percentage.toIntOrNull() ?: 40,
            blockedApps = blockedApps,
            books = books,
            activeBook = activeBook,
            aiNetwork = aiNetwork,
            aiTopics = aiTopics,
            aiLanguage = aiLanguage,
            showAllBooks = showAllBooks,
            additionalLanguage = additionalLanguage,
            profileLanguage = profileLanguage,
            selectedTopics = selectedTopics,
            totalWordsRead = totalWordsRead
        )
    }

    fun isAgeValid(): Boolean {
        return age.toIntOrNull()?.let { it in 6..15 } ?: false
    }

    companion object {
        fun fromContext(context: Context): CreateProfileUiState {
            val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val lang = prefs.getString("selected_language", "en") ?: "en"
            return CreateProfileUiState(profileLanguage = lang)
        }
    }
}
