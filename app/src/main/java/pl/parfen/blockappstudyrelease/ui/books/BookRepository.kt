package pl.parfen.blockappstudyrelease.ui.books

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.database.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.model.StorageType
import java.io.File
import java.io.IOException
import androidx.core.net.toUri

object BookRepository {

    private fun loadBooksJson(context: Context): String {
        return try {
            val inputStream = context.assets.open("books.json")
            inputStream.bufferedReader().use { it.readText() }
        } catch (_: IOException) {
            "{\"books\": []}"
        }
    }

    private fun parseBooksJson(json: String): List<Book> {
        return try {
            val gson = Gson()
            val jsonObject = gson.fromJson(json, Map::class.java)
            val booksJsonElement = gson.toJsonTree(jsonObject["books"])
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val rawBooks: List<Map<String, Any>> = gson.fromJson(booksJsonElement, type)

            rawBooks.mapNotNull { raw ->
                try {
                    val ageGroup = raw["age_group"] as? String
                    val id = (raw["id"] as? Double)?.toInt()
                    val title = raw["title"] as? String
                    val file = raw["file"] as? String
                    val language = raw["language"] as? String
                    val author = raw["author"] as? String
                    val storageTypeString = raw["storageType"] as? String

                    if (id == null || title == null || file == null || language == null) return@mapNotNull null

                    val storageType = when (storageTypeString?.uppercase()) {
                        "ASSETS" -> StorageType.ASSETS
                        "GOOGLE_DRIVE" -> StorageType.GOOGLE_DRIVE
                        else -> StorageType.INTERNAL
                    }

                    Book(
                        id = id,
                        title = title,
                        file = file,
                        language = language,
                        ageGroup = ageGroup,
                        author = author ?: "",
                        isUserBook = false,
                        storageType = storageType
                    )
                } catch (_: Exception) {
                    null
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun isAgeInRange(ageGroup: String?, age: Int): Boolean {
        if (ageGroup == null) return false
        if (ageGroup == "user") return true

        val validRanges = mapOf(
            "6-7" to (6..7),
            "8-9" to (8..9),
            "10-11" to (10..11),
            "12-13" to (12..13),
            "14-15" to (14..15)
        )

        return when {
            ageGroup.contains("-") -> validRanges[ageGroup]?.contains(age) ?: false
            else -> ageGroup.toIntOrNull()?.let { it == age } ?: false
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
    ): List<Book> = withContext(Dispatchers.IO) {
        val json = loadBooksJson(context)
        val rawBooks = parseBooksJson(json)

        val ageInt = age.toIntOrNull() ?: return@withContext emptyList()
        val normPrimary = primaryLanguage.lowercase()
        val normSecondary = secondaryLanguage?.lowercase()

        val systemBooks = rawBooks.filter { book ->
            val bookLang = book.language.lowercase()
            val langMatch = bookLang == normPrimary ||
                    (normSecondary != null && (
                            (normSecondary == "*" && bookLang != normPrimary) ||
                                    (normSecondary != "*" && bookLang == normSecondary)
                            ))
            val ageMatch = if (showAllBooks) true else isAgeInRange(book.ageGroup, ageInt)
            langMatch && ageMatch
        }.map { book ->
            try {
                book.copy(
                    pages = calculatePages(context, book),
                    progress = 0f,
                    ageGroup = book.ageGroup ?: "system",
                    storageType = book.storageType
                )
            } catch (_: Exception) {
                book.copy(pages = 1)
            }
        }

        val userBooks = if (includeUserBooks) {
            val db = AppDatabase.getDatabase(context)
            val progressDao = db.bookProgressDao()
            val rawUserBooks = progressDao.getProgressForProfile(profileId)

            val uniqueUserBooks = rawUserBooks.distinctBy { it.title + it.fileUri }
            val filteredUserBooks = uniqueUserBooks.filter { userBook ->
                systemBooks.none { systemBook ->
                    systemBook.file == userBook.file || systemBook.file == userBook.fileUri
                }
            }

            filteredUserBooks.map { progress ->
                try {
                    Book(
                        id = progress.title.hashCode(),
                        title = progress.title,
                        file = progress.file,
                        fileUri = progress.fileUri,
                        progress = progress.progress.toFloat(),
                        ageGroup = "user",
                        language = progress.language,
                        author = context.getString(R.string.user_author),
                        isUserBook = true,
                        storageType = StorageType.INTERNAL,
                        pages = calculatePages(
                            context,
                            Book(
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
                        )
                    )
                } catch (_: Exception) {
                    Book(
                        id = progress.title.hashCode(),
                        title = progress.title,
                        file = progress.file,
                        fileUri = progress.fileUri,
                        progress = progress.progress.toFloat(),
                        ageGroup = "user",
                        language = progress.language,
                        author = context.getString(R.string.user_author),
                        isUserBook = true,
                        storageType = StorageType.INTERNAL,
                        pages = 1
                    )
                }
            }
        } else {
            emptyList()
        }

        val allBooks = mutableListOf<Book>()
        allBooks.addAll(systemBooks)
        allBooks.addAll(userBooks)

        allBooks.distinctBy {
            if (it.isUserBook) "user_${it.title}_${it.fileUri}" else "system_${it.id}"
        }
    }

    private fun calculatePages(context: Context, book: Book): Int {
        return try {
            val realStorageType = if (book.file.startsWith("books/")) StorageType.ASSETS else book.storageType
            val inputStream = when (realStorageType) {
                StorageType.ASSETS -> context.assets.open(book.file)
                StorageType.INTERNAL -> File(book.file).inputStream()
                StorageType.GOOGLE_DRIVE -> context.contentResolver.openInputStream(
                    book.fileUri?.toUri() as Uri
                ) ?: throw IOException("Cannot open file from Google Drive: ${book.fileUri}")

            }
            val linesCount = inputStream.bufferedReader().useLines { it.count() }
            (linesCount / 50).coerceAtLeast(1)
        } catch (_: Exception) {
            1
        }
    }
}
