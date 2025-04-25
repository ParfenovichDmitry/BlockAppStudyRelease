package pl.parfen.blockappstudyrelease.ui.books

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.local.db.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.model.BookProgress
import pl.parfen.blockappstudyrelease.data.model.StorageType
import java.io.File

object BookManager {

    private const val SUPPORTED_DIR = "user_books"
    private const val DEFAULT_LANGUAGE = "user"
    private const val DEFAULT_AGE = "user"

    suspend fun handleUriResult(context: Context, uri: Uri, profileId: Int): Book {
        return withContext(Dispatchers.IO) {
            val fileName = getFileName(context, uri)
                ?: throw Exception(context.getString(R.string.error_file_name))

            val extension = fileName.substringAfterLast('.', "").lowercase()
            val supported = listOf("txt", "pdf", "epub", "doc", "docx")

            if (extension !in supported)
                throw Exception(context.getString(R.string.error_file_format, extension))

            val normalizedFileName = fileName
                .substringBeforeLast('.')
                .replace(" ", "_")
                .replace(Regex("[^A-Za-z0-9_]"), "")
                .lowercase()

            val userBooksDir = File(context.filesDir, SUPPORTED_DIR).apply { mkdirs() }
            val destFile = File(userBooksDir, "$normalizedFileName.$extension")

            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            } ?: throw Exception(context.getString(R.string.error_open_stream))

            val book = Book(
                id = 1000 + destFile.hashCode(),
                title = fileName,
                file = destFile.absolutePath,
                language = DEFAULT_LANGUAGE,
                ageGroup = DEFAULT_AGE,
                author = context.getString(R.string.user_author),
                isUserBook = true,
                progress = 0f,
                fileUri = uri.toString(),
                storageType = StorageType.INTERNAL
            )

            saveUserBookToDb(context, profileId, book)
            return@withContext book
        }
    }

    suspend fun saveBookChanges(context: Context, profileId: Int, books: List<Book>, selectedBookTitle: String) {
        val db = AppDatabase.getDatabase(context)
        val progressDao = db.bookProgressDao()

        books.forEach { book ->
            val progress = book.progress.toDouble()
            val existing = progressDao.getProgressForBook(profileId, book.title)

            if (existing == null) {
                progressDao.insert(
                    BookProgress(
                        profileId = profileId,
                        title = book.title,
                        file = book.file,
                        progress = progress,
                        language = book.language,
                        fileUri = book.fileUri
                    )
                )
            } else {
                progressDao.updateProgress(profileId, book.title, progress)
                progressDao.updateLanguage(profileId, book.title, book.language)
            }
        }

        db.profileDao().getProfileById(profileId)?.let { profile ->
            db.profileDao().update(
                profile.copy(
                    books = books.map { it.title }.distinct(),
                    activeBook = selectedBookTitle
                )
            )
        }
    }

    suspend fun deleteUserBook(context: Context, profileId: Int, book: Book) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            val progressDao = db.bookProgressDao()

            if (book.storageType == StorageType.INTERNAL) {
                val file = File(book.file)
                if (file.exists()) file.delete()
            }

            progressDao.deleteByProfileIdAndTitle(profileId, book.title)
        }
    }

    private suspend fun saveUserBookToDb(context: Context, profileId: Int, book: Book) {
        val db = AppDatabase.getDatabase(context)
        val progressDao = db.bookProgressDao()

        val existing = progressDao.getProgressForBook(profileId, book.title)
        if (existing == null) {
            progressDao.insert(
                BookProgress(
                    profileId = profileId,
                    title = book.title,
                    file = book.file,
                    progress = book.progress.toDouble(),
                    language = book.language,
                    fileUri = book.fileUri
                )
            )
        } else {
            progressDao.update(
                existing.copy(
                    file = book.file,
                    language = book.language,
                    fileUri = book.fileUri
                )
            )
        }
    }

    suspend fun saveUiStateSettings(
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

    private fun getFileName(context: Context, uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex("_display_name")
            if (cursor.moveToFirst() && nameIndex >= 0) {
                cursor.getString(nameIndex)
            } else null
        }
    }
}
