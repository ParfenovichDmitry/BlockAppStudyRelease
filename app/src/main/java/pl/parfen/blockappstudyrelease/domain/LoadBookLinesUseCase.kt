package pl.parfen.blockappstudyrelease.domain

import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.repository.BookRepositoryImpl

class LoadBookLinesUseCase(
    private val repository: BookRepositoryImpl
) {

    suspend fun countBookLines(book: Book): Int {
        return repository.countBookLines(book)
    }

    suspend fun loadBookLines(book: Book, startLine: Int, linesToRead: Int): List<String> {
        return repository.loadBookLines(book, startLine, linesToRead)
    }
}
