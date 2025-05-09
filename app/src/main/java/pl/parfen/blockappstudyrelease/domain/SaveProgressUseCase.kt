package pl.parfen.blockappstudyrelease.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.repository.BookRepositoryImpl

class SaveProgressUseCase(
    private val repository: BookRepositoryImpl
) {

    suspend fun saveProgress(
        profileId: Int,
        book: Book,
        progressPercent: Float
    ) = withContext(Dispatchers.IO) {
        repository.saveProgress(profileId, book, progressPercent)
    }
}
