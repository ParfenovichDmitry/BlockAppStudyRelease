package pl.parfen.blockappstudyrelease.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.BookPreviewActivity
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.database.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.ui.books.*

class BookViewModel(
    @SuppressLint("StaticFieldLeak") private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val PREFS_NAME = "book_settings"
        private const val KEY_SHOW_ALL_BOOKS = "showAllBooks"
        private const val KEY_SECONDARY_LANGUAGE = "secondaryLanguage"
        private const val KEY_ACTIVE_BOOK = "activeBook"
        private const val KEY_SCROLL_POSITION_PREFIX = "scrollPosition_"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    fun restoreUiStateFromProfile(profileId: Int, age: String, primaryLanguage: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val addedBooks = if (profileId == -1)
                savedStateHandle.get<List<Book>>("addedUserBooks") ?: emptyList()
            else emptyList()

            val showAllBooks = if (profileId == -1)
                prefs.getBoolean(KEY_SHOW_ALL_BOOKS, false)
            else AppDatabase.getDatabase(context).profileDao().getProfileById(profileId)?.showAllBooks ?: false

            val secondaryLanguage = if (profileId == -1)
                prefs.getString(KEY_SECONDARY_LANGUAGE, null)
            else AppDatabase.getDatabase(context).profileDao().getProfileById(profileId)?.additionalLanguage

            val selectedBook = if (profileId == -1)
                prefs.getString(KEY_ACTIVE_BOOK, "") ?: ""
            else AppDatabase.getDatabase(context).profileDao().getProfileById(profileId)?.activeBook ?: ""

            val scrollPosition = if (selectedBook.isNotEmpty()) {
                val savedPosition = prefs.getInt("$KEY_SCROLL_POSITION_PREFIX$selectedBook", 0)
                Log.d("BookRestore", "Restoring scroll position: $savedPosition for book: $selectedBook")
                savedPosition
            } else {
                0
            }

            val languageNames = context.resources.getStringArray(R.array.available_languages).toList()
            val filteredLanguages = languageNames.filter { it != primaryLanguage }
            val selectedLangIndex = secondaryLanguage?.let { filteredLanguages.indexOf(it) } ?: -1

            savedStateHandle["currentAge"] = age

            val includeUserBooks = profileId == -1
            val books = BookRepository.getAllBooks(context, age, primaryLanguage, secondaryLanguage, showAllBooks, includeUserBooks, profileId)

            val allBooks = mutableListOf<Book>().apply {
                addAll(books)
                addedBooks.forEach { userBook ->
                    if (none { it.title == userBook.title && it.fileUri == userBook.fileUri }) add(userBook)
                }
            }

            // Исправлено: правильное определение активной книги!
            val effectiveSelectedBook = if (selectedBook.isNotEmpty() && allBooks.any { it.title == selectedBook }) {
                selectedBook
            } else {
                allBooks.minByOrNull { it.id }?.title.orEmpty()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    showAllBooks = showAllBooks,
                    secondaryLanguage = secondaryLanguage,
                    selectedBookTitle = effectiveSelectedBook,
                    selectedAdditionalLanguageIndex = selectedLangIndex,
                    profileId = profileId,
                    age = age,
                    primaryLanguage = primaryLanguage,
                    scrollPosition = scrollPosition,
                    isProfileLoaded = true,
                    showUserBooks = includeUserBooks,
                    addedUserBooks = addedBooks,
                    books = allBooks
                )
            }
        }
    }

    fun loadBooks(age: String, primaryLanguage: String, secondaryLanguage: String?, showAllBooks: Boolean, includeUserBooks: Boolean, profileId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    books = emptyList(),
                    age = age,
                    primaryLanguage = primaryLanguage,
                    showUserBooks = includeUserBooks
                )
            }

            try {
                val books = BookRepository.getAllBooks(context, age, primaryLanguage, secondaryLanguage, showAllBooks, includeUserBooks, profileId)
                val defaultBook = books.minByOrNull { it.id }
                val addedUserBooks = _uiState.value.addedUserBooks

                val allBooks = mutableListOf<Book>().apply {
                    addAll(books)
                    addedUserBooks.forEach { userBook ->
                        if (none { it.title == userBook.title && it.fileUri == userBook.fileUri }) add(userBook)
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        books = allBooks,
                        selectedBookTitle = if (it.selectedBookTitle.isNotEmpty() && allBooks.any { b -> b.title == it.selectedBookTitle })
                            it.selectedBookTitle
                        else
                            defaultBook?.title.orEmpty()
                    )
                }
            } catch (_: Exception) {
                val error = context.getString(R.string.error_add_book, "loading books")
                _uiState.update { it.copy(isLoading = false, error = error) }
            }
        }
    }

    fun addUserBook(uri: Uri, profileId: Int) {
        viewModelScope.launch {
            val currentAdded = _uiState.value.addedUserBooks
            if (currentAdded.any { it.fileUri == uri.toString() }) {
                val error = context.getString(R.string.error_add_book, "book already exists")
                _uiState.update { it.copy(error = error) }
                return@launch
            }
            val newBook = BookManager.handleUriResult(context, uri, profileId)
            val updatedBooks = currentAdded.toMutableList().apply { add(newBook) }
            savedStateHandle["addedUserBooks"] = updatedBooks
            _uiState.update { it.copy(addedUserBooks = updatedBooks) }
            loadBooks(_uiState.value.age, _uiState.value.primaryLanguage, _uiState.value.secondaryLanguage, _uiState.value.showAllBooks, profileId == -1, profileId)
        }
    }

    fun deleteUserBook(book: Book, profileId: Int) {
        viewModelScope.launch {
            try {
                BookManager.deleteUserBook(context, profileId, book)
                val updated = _uiState.value.addedUserBooks.filterNot { it.fileUri == book.fileUri }
                savedStateHandle["addedUserBooks"] = updated
                _uiState.update { it.copy(addedUserBooks = updated) }
                loadBooks(_uiState.value.age, _uiState.value.primaryLanguage, _uiState.value.secondaryLanguage, _uiState.value.showAllBooks, profileId == -1, profileId)
            } catch (_: Exception) {
                val error = context.getString(R.string.error_delete_book, book.title)
                _uiState.update { it.copy(error = error) }
            }
        }
    }

    fun saveScrollPosition(bookTitle: String, position: Int) {
        prefs.edit {
            putInt("$KEY_SCROLL_POSITION_PREFIX$bookTitle", position)
        }
        savedStateHandle["$KEY_SCROLL_POSITION_PREFIX$bookTitle"] = position
        _uiState.update { it.copy(scrollPosition = position) }
        Log.d("BookSave", "Saved scroll position: $position for book: $bookTitle")
    }

    fun getScrollPosition(bookTitle: String): Int {
        val position = prefs.getInt("$KEY_SCROLL_POSITION_PREFIX$bookTitle", 0)
        Log.d("BookRestore", "Retrieved scroll position: $position for book: $bookTitle")
        return position
    }

    fun selectBook(bookTitle: String) {
        // ВСЕГДА сохраняем и в uiState, и в prefs, и в savedStateHandle
        _uiState.update { it.copy(selectedBookTitle = bookTitle) }
        prefs.edit { putString(KEY_ACTIVE_BOOK, bookTitle) }
        savedStateHandle["activeBook"] = bookTitle
    }

    fun updateShowAllBooks(value: Boolean) {
        prefs.edit { putBoolean(KEY_SHOW_ALL_BOOKS, value) }
        savedStateHandle["showAllBooks"] = value
        _uiState.update { it.copy(showAllBooks = value) }
        loadBooks(_uiState.value.age, _uiState.value.primaryLanguage, _uiState.value.secondaryLanguage, value, _uiState.value.profileId == -1, _uiState.value.profileId)
    }

    fun updateSelectedAdditionalLanguage(language: String?) {
        prefs.edit { putString(KEY_SECONDARY_LANGUAGE, language) }
        savedStateHandle["secondaryLanguage"] = language
        _uiState.update { it.copy(secondaryLanguage = language) }
        loadBooks(_uiState.value.age, _uiState.value.primaryLanguage, language, _uiState.value.showAllBooks, _uiState.value.profileId == -1, _uiState.value.profileId)
    }

    fun openPreview(context: Context, book: Book) {
        val intent = Intent(context, BookPreviewActivity::class.java).apply {
            putExtra("book", book)
            putExtra("scrollPosition", getScrollPosition(book.title))
            putExtra("profileId", _uiState.value.profileId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun saveChanges(profileId: Int, showAllBooks: Boolean, secondaryLanguage: String?, onSaveComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val selectedBook = _uiState.value.selectedBookTitle

            // ОБЯЗАТЕЛЬНО сохраняем выбранную книгу ПЕРЕД обновлением профиля!
            selectBook(selectedBook)

            prefs.edit {
                putBoolean(KEY_SHOW_ALL_BOOKS, showAllBooks)
                putString(KEY_SECONDARY_LANGUAGE, secondaryLanguage)
                putString(KEY_ACTIVE_BOOK, selectedBook)
            }

            savedStateHandle["showAllBooks"] = showAllBooks
            savedStateHandle["secondaryLanguage"] = secondaryLanguage
            savedStateHandle["activeBook"] = selectedBook

            _uiState.update {
                it.copy(
                    showAllBooks = showAllBooks,
                    secondaryLanguage = secondaryLanguage,
                    selectedBookTitle = selectedBook
                )
            }

            if (profileId != -1) {
                saveUiSettings(context, profileId, showAllBooks, secondaryLanguage, selectedBook)
                cancelChanges()
            }

            onSaveComplete(showAllBooks, secondaryLanguage)
        }
    }

    fun cancelChanges() {
        viewModelScope.launch {
            val keysToRemove = savedStateHandle.keys().filter {
                it !in listOf("addedUserBooks", "showAllBooks", "secondaryLanguage", "activeBook") &&
                        !it.startsWith(KEY_SCROLL_POSITION_PREFIX)
            }
            keysToRemove.forEach { savedStateHandle.remove<Any>(it) }

            restoreUiStateFromProfile(
                _uiState.value.profileId,
                _uiState.value.age,
                _uiState.value.primaryLanguage
            )
        }
    }

    private fun saveUiSettings(context: Context, profileId: Int, showAllBooks: Boolean, secondaryLanguage: String?, selectedBook: String) {
        viewModelScope.launch {
            try {
                val profileDao = AppDatabase.getDatabase(context).profileDao()
                val profile = profileDao.getProfileById(profileId)
                profile?.let {
                    profileDao.update(
                        it.copy(
                            showAllBooks = showAllBooks,
                            additionalLanguage = secondaryLanguage,
                            activeBook = selectedBook // <-- сохраняем выбранную книгу!
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error saving UI settings: ${e.message}")
            }
        }
    }
}
