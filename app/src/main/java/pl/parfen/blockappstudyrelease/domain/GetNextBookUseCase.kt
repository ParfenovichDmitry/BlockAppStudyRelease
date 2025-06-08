package pl.parfen.blockappstudyrelease.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.repository.BookRepositoryImpl

class GetNextBookUseCase(
    private val repository: BookRepositoryImpl
) {
    suspend fun getNextBook(currentBook: Book, profileId: Int, selectedBook: Book? = null): Book? = withContext(Dispatchers.IO) {
        repository.getNextSystemBook(currentBook, profileId, selectedBook)
    }
}
