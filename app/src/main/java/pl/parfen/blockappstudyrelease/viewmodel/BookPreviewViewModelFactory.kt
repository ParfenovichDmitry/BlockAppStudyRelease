package pl.parfen.blockappstudyrelease.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.parfen.blockappstudyrelease.data.repository.BookRepositoryImpl
import pl.parfen.blockappstudyrelease.domain.LoadBookLinesUseCase

import pl.parfen.blockappstudyrelease.domain.usecase.SaveProgressUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.GetNextBookUseCase

class BookPreviewViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookPreviewViewModel::class.java)) {
            val repository = BookRepositoryImpl(context)

            val loadBookLinesUseCase = LoadBookLinesUseCase(repository)
            val saveProgressUseCase = SaveProgressUseCase(repository)
            val getNextBookUseCase = GetNextBookUseCase(repository)

            return BookPreviewViewModel(
                loadBookLinesUseCase = loadBookLinesUseCase,
                saveProgressUseCase = saveProgressUseCase,
                getNextBookUseCase = getNextBookUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
