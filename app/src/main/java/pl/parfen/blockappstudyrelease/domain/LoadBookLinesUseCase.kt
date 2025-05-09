package pl.parfen.blockappstudyrelease.domain

import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.repository.BookRepositoryImpl

class LoadBookLinesUseCase(
    private val repository: BookRepositoryImpl
) {
    suspend fun countBookLines(book: Book): Int =
        repository.countBookLines(book)

    suspend fun loadBookLines(book: Book, startLine: Int, linesToRead: Int): List<String> =
        repository.loadBookLines(book, startLine, linesToRead)
}
