package pl.parfen.blockappstudyrelease.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.BookPreviewActivity
import pl.parfen.blockappstudyrelease.data.local.db.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.ui.books.BookManager
import pl.parfen.blockappstudyrelease.ui.books.BookRepository
import pl.parfen.blockappstudyrelease.ui.books.BookUiState
import pl.parfen.blockappstudyrelease.ui.books.Quadruple

class BookViewModel(
    @SuppressLint("StaticFieldLeak") private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    fun restoreUiStateFromProfile(profileId: Int, age: String, primaryLanguage: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val db = AppDatabase.getDatabase(context)
            val profile = db.profileDao().getProfileById(profileId)
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            val showAllBooksHandle = savedStateHandle.get<Boolean>("showAllBooks")
            val secondaryLanguageHandle = savedStateHandle.get<String>("secondaryLanguage")
            val selectedBookTitleHandle = savedStateHandle.get<String>("activeBook")
            val scrollPositionHandle = savedStateHandle.get<Int>("scrollPosition") ?: 0

            if (profileId == -1) {
                val storedShowAllBooks = prefs.getBoolean(KEY_SHOW_ALL_BOOKS, false)
                val storedSecondaryLanguage = prefs.getString(KEY_SECONDARY_LANGUAGE, null)
                val storedSelectedBookTitle = prefs.getString(KEY_ACTIVE_BOOK, "") ?: ""
                val storedScrollPosition = prefs.getInt(KEY_SCROLL_POSITION, 0)

                _uiState.update {
                    it.copy(
                        showAllBooks = showAllBooksHandle ?: storedShowAllBooks,
                        secondaryLanguage = secondaryLanguageHandle ?: storedSecondaryLanguage,
                        selectedBookTitle = selectedBookTitleHandle ?: storedSelectedBookTitle,
                        selectedAdditionalLanguageIndex = -1,
                        profileId = profileId,
                        age = age,
                        primaryLanguage = primaryLanguage,
                        scrollPosition = scrollPositionHandle.takeIf { it != 0 } ?: storedScrollPosition,
                        isProfileLoaded = true
                    )
                }
            } else {
                val (restoredShowAllBooks, restoredSecondaryLanguage, restoredSelectedBookTitle, restoredScrollPosition) = if (profile == null) {
                    Quadruple(false, null, "", 0)
                } else {
                    Quadruple(profile.showAllBooks, profile.additionalLanguage,
                        profile.activeBook, 0)
                }

                val languageNames = context.resources.getStringArray(pl.parfen.blockappstudyrelease.R.array.available_languages).toList()
                val filteredLanguages = languageNames.filter { it != primaryLanguage }
                val index = if (restoredSecondaryLanguage != null && restoredSecondaryLanguage != primaryLanguage) {
                    filteredLanguages.indexOf(restoredSecondaryLanguage)
                } else {
                    -1
                }

                _uiState.update {
                    it.copy(
                        showAllBooks = showAllBooksHandle ?: restoredShowAllBooks,
                        secondaryLanguage = secondaryLanguageHandle ?: restoredSecondaryLanguage,
                        selectedBookTitle = selectedBookTitleHandle ?: restoredSelectedBookTitle,
                        selectedAdditionalLanguageIndex = index,
                        profileId = profileId,
                        age = age,
                        primaryLanguage = primaryLanguage,
                        scrollPosition = scrollPositionHandle.takeIf { it != 0 } ?: restoredScrollPosition,
                        isProfileLoaded = true
                    )
                }
            }

            loadBooks(age, primaryLanguage, uiState.value.secondaryLanguage, uiState.value.showAllBooks, profileId)
        }
    }

    fun loadBooks(age: String, primaryLanguage: String, secondaryLanguage: String?, showAllBooks: Boolean, profileId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                BookRepository.clearCache()
                val books = BookRepository.getAllBooks(context, age, primaryLanguage, secondaryLanguage, showAllBooks, true, profileId)
                val defaultBook = books.minByOrNull { it.id }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        books = books,
                        selectedBookTitle = it.selectedBookTitle.ifEmpty { defaultBook?.title.orEmpty() }
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error loading books") }
            }
        }
    }

    fun selectBook(bookTitle: String) {
        _uiState.update { it.copy(selectedBookTitle = bookTitle) }
        savedStateHandle["activeBook"] = bookTitle
    }

    fun updateShowAllBooks(value: Boolean) {
        _uiState.update { it.copy(showAllBooks = value) }
        savedStateHandle["showAllBooks"] = value
    }

    fun updateSelectedAdditionalLanguage(language: String?) {
        _uiState.update { it.copy(secondaryLanguage = language) }
        savedStateHandle["secondaryLanguage"] = language
    }

    fun saveScrollPosition(position: Int) {
        _uiState.update { it.copy(scrollPosition = position) }
        savedStateHandle["scrollPosition"] = position
    }

    fun cancelChanges() {
        savedStateHandle.remove<Boolean>("showAllBooks")
        savedStateHandle.remove<String>("secondaryLanguage")
        savedStateHandle.remove<String>("activeBook")
        savedStateHandle.remove<Int>("scrollPosition")
    }

    fun saveChanges(
        profileId: Int,
        showAllBooks: Boolean,
        secondaryLanguage: String?,
        onSaveComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()

            if (profileId == -1) {
                prefs.putBoolean(KEY_SHOW_ALL_BOOKS, showAllBooks)
                prefs.putString(KEY_SECONDARY_LANGUAGE, secondaryLanguage)
                prefs.putString(KEY_ACTIVE_BOOK, _uiState.value.selectedBookTitle)
                prefs.putInt(KEY_SCROLL_POSITION, _uiState.value.scrollPosition)
                prefs.apply()

                savedStateHandle["showAllBooks"] = showAllBooks
                savedStateHandle["secondaryLanguage"] = secondaryLanguage
                savedStateHandle["activeBook"] = _uiState.value.selectedBookTitle
                savedStateHandle["scrollPosition"] = _uiState.value.scrollPosition
            } else {
                val db = AppDatabase.getDatabase(context)
                val profile = db.profileDao().getProfileById(profileId)
                if (profile != null) {
                    val updated = profile.copy(
                        showAllBooks = showAllBooks,
                        additionalLanguage = secondaryLanguage,
                        activeBook = _uiState.value.selectedBookTitle
                    )
                    db.profileDao().update(updated)
                }
                cancelChanges()
            }

            _uiState.update {
                it.copy(
                    showAllBooks = showAllBooks,
                    secondaryLanguage = secondaryLanguage
                )
            }

            onSaveComplete(showAllBooks, secondaryLanguage)
        }
    }

    fun addUserBook(uri: Uri, profileId: Int) {
        viewModelScope.launch {
            try {
                val newBook = BookManager.handleUriResult(context, uri, profileId)
                _uiState.update {
                    val updatedBooks = it.books + newBook
                    it.copy(
                        books = updatedBooks,
                        selectedBookTitle = it.selectedBookTitle.ifEmpty { newBook.title }
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(error = "Error adding book") }
            }
        }
    }

    fun deleteUserBook(book: Book, profileId: Int) {
        if (!book.isUserBook) return

        viewModelScope.launch {
            try {
                BookManager.deleteUserBook(context, profileId, book)
                _uiState.update {
                    val updatedBooks = it.books.filter { b -> b.title != book.title }
                    val newSelected = if (book.title == it.selectedBookTitle) {
                        updatedBooks.firstOrNull()?.title.orEmpty()
                    } else it.selectedBookTitle
                    it.copy(books = updatedBooks, selectedBookTitle = newSelected)
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(error = "Error deleting book") }
            }
        }
    }

    fun openPreview(book: Book, profileId: Int) {
        val intent = Intent(context, BookPreviewActivity::class.java).apply {
            putExtra("bookTitle", book.title)
            putExtra("bookFile", book.file)
            putExtra("fileUri", book.fileUri)
            putExtra("progress", book.progress)
            putExtra("profile_id", profileId)
        }
        context.startActivity(intent)
    }

    companion object {
        private const val PREFS_NAME = "book_settings"
        private const val KEY_SHOW_ALL_BOOKS = "showAllBooks_temp"
        private const val KEY_SECONDARY_LANGUAGE = "secondaryLanguage_temp"
        private const val KEY_ACTIVE_BOOK = "activeBook_temp"
        private const val KEY_SCROLL_POSITION = "scrollPosition_temp"
    }
}
