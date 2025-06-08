package pl.parfen.blockappstudyrelease.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.data.database.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.model.BookProgress
import pl.parfen.blockappstudyrelease.data.model.StorageType
import pl.parfen.blockappstudyrelease.data.repository.blockapp.ProfileRepository
import pl.parfen.blockappstudyrelease.domain.FileLoader
import pl.parfen.blockappstudyrelease.ui.books.BookRepository as JsonBookRepository
import java.io.File

class BookRepositoryImpl(private val context: Context) {

    private val profileRepository = ProfileRepository(context)

    suspend fun getSystemBooks(profileId: Int): List<Book> = withContext(Dispatchers.IO) {
        val profile = profileRepository.getProfileById(profileId)
        if (profile == null) return@withContext emptyList()

        val age = profile.age.toString()
        val lang = profile.profileLanguage.ifBlank { "pl" }
        val secondaryLang = profile.additionalLanguage ?: "*"
        val showAll = profile.showAllBooks

        JsonBookRepository.getAllBooks(
            context = context,
            age = age,
            primaryLanguage = lang,
            secondaryLanguage = secondaryLang,
            showAllBooks = showAll,
            includeUserBooks = false,
            profileId = profileId
        ).filter { !it.isUserBook }
    }

    suspend fun getUserBooks(profileId: Int): List<Book> = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)
        val userProgressList = db.bookProgressDao().getProgressForProfile(profileId)
        userProgressList.map {
            Book(
                id = it.title.hashCode(),
                title = it.title,
                file = it.file,
                fileUri = it.fileUri,
                progress = it.progress.toFloat(),
                ageGroup = "user",
                language = it.language,
                author = "User",
                isUserBook = true,
                storageType = StorageType.INTERNAL
            )
        }
    }

    suspend fun loadBookLines(book: Book, startLine: Int, linesToRead: Int): List<String> =
        withContext(Dispatchers.IO) {
            when (book.storageType) {
                StorageType.ASSETS -> FileLoader.extractTextFromAssetsPart(context, book.file, startLine, linesToRead)
                StorageType.INTERNAL -> {
                    val uri = Uri.fromFile(File(book.file))
                    FileLoader.extractTextPart(context, uri, getExtension(book.file), startLine, linesToRead)
                }
                StorageType.GOOGLE_DRIVE -> {
                    val uri = book.fileUri?.let { Uri.parse(it) }
                    if (uri != null) {
                        FileLoader.extractTextPart(context, uri, getExtension(book.file), startLine, linesToRead)
                    } else {
                        emptyList()
                    }
                }
            }
        }

    suspend fun countBookLines(book: Book): Int = withContext(Dispatchers.IO) {
        when (book.storageType) {
            StorageType.ASSETS -> FileLoader.countTotalLinesFromAssets(context, book.file)
            StorageType.INTERNAL -> FileLoader.countTotalLines(context, Uri.fromFile(File(book.file)), getExtension(book.file))
            StorageType.GOOGLE_DRIVE -> {
                val uri = book.fileUri?.let { Uri.parse(it) }
                if (uri != null) {
                    FileLoader.countTotalLines(context, uri, getExtension(book.file))
                } else {
                    0
                }
            }
        }
    }

    private fun getExtension(filePath: String): String {
        return filePath.substringAfterLast('.', "").lowercase()
    }

    suspend fun saveProgress(profileId: Int, book: Book, progressPercent: Float) {
        val db = AppDatabase.getDatabase(context)
        val progressDao = db.bookProgressDao()

        val existing = progressDao.getProgressForBook(profileId, book.title)
        val newProgress = progressPercent.coerceIn(0f, 100f).toDouble()

        val effectiveFileUri = if (book.isUserBook) {
            book.file
        } else {
            book.fileUri ?: book.file
        }

        if (existing == null) {
            progressDao.insert(
                BookProgress(
                    profileId = profileId,
                    title = book.title,
                    file = book.file,
                    fileUri = effectiveFileUri,
                    language = book.language,
                    progress = newProgress
                )
            )
        } else {
            progressDao.update(
                existing.copy(
                    progress = newProgress,
                    fileUri = effectiveFileUri
                )
            )
        }
    }

    suspend fun getNextSystemBook(currentBook: Book, profileId: Int, selectedBook: Book? = null): Book? {
        val systemBooks = getSystemBooks(profileId)
        if (systemBooks.isEmpty()) return null

        val currentIndex = systemBooks.indexOfFirst { it.id == currentBook.id }
        val selectedIndex = selectedBook?.let { systemBooks.indexOfFirst { b -> b.id == it.id } } ?: 0

        if (currentIndex == -1) return systemBooks.firstOrNull()

        var nextIndex = currentIndex + 1
        if (nextIndex >= systemBooks.size) nextIndex = 0

        val nextBook = systemBooks[nextIndex]

        return if (selectedBook != null && nextBook.id == selectedBook.id) null else nextBook
    }
}
