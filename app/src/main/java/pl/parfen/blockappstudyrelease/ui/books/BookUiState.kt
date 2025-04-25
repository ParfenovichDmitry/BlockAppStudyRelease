package pl.parfen.blockappstudyrelease.ui.books

import android.content.Context
import pl.parfen.blockappstudyrelease.data.local.db.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Book

data class BookUiState(
    val isLoading: Boolean = true,
    val books: List<Book> = emptyList(),
    val selectedBookTitle: String = "",
    val showAllBooks: Boolean = false,
    val primaryLanguage: String = "",
    val secondaryLanguage: String? = null,
    val availableLanguages: List<String> = emptyList(),
    val selectedLanguageIndex: Int = 0,
    val selectedAdditionalLanguageIndex: Int = -1,
    val profileId: Int = -1,
    val age: String = "",
    val appsSavedText: String = "",
    val error: String? = null,
    val isProfileLoaded: Boolean = false,
    val scrollPosition: Int = 0
    )
suspend fun saveUiSettings(
    context: Context,
    profileId: Int,
    showAllBooks: Boolean,
    secondaryLanguage: String?
) {
    val db = AppDatabase.getDatabase(context)
    val profileDao = db.profileDao()

    val profile = profileDao.getProfileById(profileId)
    if (profile != null) {
        profileDao.update(
            profile.copy(
                showAllBooks = showAllBooks,
                additionalLanguage = secondaryLanguage
            )
        )
    }
}

