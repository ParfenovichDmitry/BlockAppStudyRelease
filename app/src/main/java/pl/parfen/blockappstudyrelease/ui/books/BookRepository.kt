package pl.parfen.blockappstudyrelease.ui.books

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.local.db.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.model.StorageType
import java.io.File
import java.io.IOException

object BookRepository {

    private var cachedBooksJson: String? = null
    private var cachedParsedBooks: List<Book>? = null

    fun loadBooksJson(context: Context): String {
        cachedBooksJson?.let { return it }
        return try {
            val inputStream = context.assets.open("books.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            cachedBooksJson = json
            json
        } catch (_: Exception) {
            "{\"books\": []}".also { cachedBooksJson = it }
        }
    }

    fun parseBooksJson(json: String): List<Book> {
        cachedParsedBooks?.let { return it }
        return try {
            val jsonObject = Gson().fromJson(json, Map::class.java)
            val booksJsonElement = Gson().toJsonTree(jsonObject["books"])
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val rawBooks: List<Map<String, Any>> = Gson().fromJson(booksJsonElement, type)

            val books = rawBooks.mapNotNull { raw ->
                try {
                    Book(
                        id = (raw["id"] as? Double)?.toInt() ?: return@mapNotNull null,
                        title = raw["title"] as? String ?: return@mapNotNull null,
                        file = raw["file"] as? String ?: return@mapNotNull null,
                        language = raw["language"] as? String ?: return@mapNotNull null,
                        ageGroup = raw["age_group"] as? String,
                        author = raw["author"] as? String ?: "",
                        isUserBook = false,
                        storageType = when ((raw["storageType"] as? String)?.uppercase()) {
                            "ASSETS" -> StorageType.ASSETS
                            "GOOGLE_DRIVE" -> StorageType.GOOGLE_DRIVE
                            else -> StorageType.INTERNAL
                        }
                    )
                } catch (_: Exception) {
                    null
                }
            }

            cachedParsedBooks = books
            books
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getBooksForAgeAndLanguage(
        books: List<Book>,
        age: String,
        languages: List<String>,
        showAllBooks: Boolean
    ): List<Book> {
        val normalizedLanguages = languages.map { it.trim().lowercase() }
        val ageInt = age.toIntOrNull() ?: 0

        return books.filter { book ->
            val ageGroup = book.ageGroup ?: return@filter false
            val isAgeMatch = showAllBooks || ageGroup == "all" || isAgeInRange(ageGroup, ageInt)
            val isLanguageMatch = book.language.lowercase() in normalizedLanguages || book.language == "user"
            isAgeMatch && isLanguageMatch
        }
    }

    private fun isAgeInRange(ageGroup: String, age: Int): Boolean {
        return try {
            if (ageGroup.contains("-")) {
                val (start, end) = ageGroup.split("-").mapNotNull { it.toIntOrNull() }
                age in start..end
            } else {
                ageGroup.toIntOrNull() == age
            }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getAllBooks(
        context: Context,
        age: String,
        primaryLanguage: String,
        secondaryLanguage: String?,
        showAllBooks: Boolean,
        includeUserBooks: Boolean,
        profileId: Int
    ): List<Book> {
        clearCache()

        val json = loadBooksJson(context)
        val rawBooks = parseBooksJson(json)

        val ageInt = age.toIntOrNull() ?: 0
        val ageRange = when {
            ageInt <= 7 -> "6-7"
            ageInt <= 9 -> "8-9"
            ageInt <= 11 -> "10-11"
            ageInt <= 13 -> "12-13"
            else -> "14-15"
        }

        val normalizedPrimary = primaryLanguage.lowercase()
        val normalizedSecondary = secondaryLanguage?.lowercase()
        val useSecondary = !normalizedSecondary.isNullOrEmpty()

        val filteredBooks = rawBooks.filter { book ->
            val bookLang = book.language.lowercase()
            val isLangMatch = if (useSecondary) {
                bookLang == normalizedPrimary || bookLang == normalizedSecondary
            } else {
                bookLang == normalizedPrimary
            }

            val isAgeMatch = if (showAllBooks) true else {
                book.ageGroup == "all" || book.ageGroup == ageRange
            }

            isLangMatch && isAgeMatch
        }

        val systemBooks = filteredBooks.map { book ->
            try {
                book.copy(
                    pages = calculatePages(context, book),
                    progress = 0f,
                    ageGroup = book.ageGroup ?: "user",
                    storageType = book.storageType
                )
            } catch (_: Exception) {
                book.copy(
                    pages = 1,
                    ageGroup = book.ageGroup ?: "user",
                    storageType = book.storageType
                )
            }
        }

        val userBooks = if (includeUserBooks) {
            val db = AppDatabase.getDatabase(context)
            val progressDao = db.bookProgressDao()
            progressDao.getProgressForProfile(profileId).map { progress ->
                val book = Book(
                    id = progress.title.hashCode(),
                    title = progress.title,
                    file = progress.file,
                    fileUri = progress.fileUri,
                    progress = progress.progress.toFloat(),
                    ageGroup = "user",
                    language = progress.language,
                    author = context.getString(R.string.user_author),
                    isUserBook = true,
                    storageType = StorageType.INTERNAL
                )
                book.copy(pages = calculatePages(context, book))
            }
        } else emptyList()

        return (systemBooks + userBooks).distinctBy { it.title }
    }

    private fun calculatePages(context: Context, book: Book): Int {
        return try {
            val realStorageType = if (book.file.startsWith("books/")) StorageType.ASSETS else book.storageType
            val inputStream = when (realStorageType) {
                StorageType.ASSETS -> context.assets.open(book.file)
                StorageType.INTERNAL -> File(book.file).inputStream()
                StorageType.GOOGLE_DRIVE -> context.contentResolver.openInputStream(Uri.parse(book.fileUri))
                    ?: throw IOException("Cannot open file from Google Drive")
            }
            val linesCount = inputStream.bufferedReader().useLines { it.count() }
            (linesCount / 50).coerceAtLeast(1)
        } catch (_: Exception) {
            1
        }
    }

    fun clearCache() {
        cachedBooksJson = null
        cachedParsedBooks = null
    }

    fun addBook(context: Context, book: Book) {}
    fun removeBook(book: Book) {}
}
