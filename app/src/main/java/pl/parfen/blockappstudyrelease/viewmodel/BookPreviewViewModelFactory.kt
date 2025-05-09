package pl.parfen.blockappstudyrelease.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.parfen.blockappstudyrelease.domain.LoadBookLinesUseCase
import pl.parfen.blockappstudyrelease.domain.SaveProgressUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.GetNextBookUseCase


class BookPreviewViewModelFactory(
    private val loadBookLinesUseCase: LoadBookLinesUseCase,
    private val saveProgressUseCase: SaveProgressUseCase,
    private val getNextBookUseCase: GetNextBookUseCase,
    private val bookViewModel: BookViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookPreviewViewModel::class.java)) {
            return BookPreviewViewModel(
                loadBookLinesUseCase,
                saveProgressUseCase,
                getNextBookUseCase,
                bookViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}