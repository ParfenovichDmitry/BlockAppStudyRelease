package pl.parfen.blockappstudyrelease.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.domain.LoadBookLinesUseCase
import pl.parfen.blockappstudyrelease.domain.SaveProgressUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.GetNextBookUseCase

import pl.parfen.blockappstudyrelease.ui.preview.BookPreviewState

class BookPreviewViewModel(
    private val loadBookLinesUseCase: LoadBookLinesUseCase,
    private val saveProgressUseCase: SaveProgressUseCase,
    private val getNextBookUseCase: GetNextBookUseCase,
    private val bookViewModel: BookViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookPreviewState())
    val uiState: StateFlow<BookPreviewState> = _uiState

    private val linesPerLoad = 500
    private var totalLines = 0
    private var loadedLines = 0
    private var profileId: Int = -1
    private var hasRestoredScroll = false

    fun init(profileId: Int, initialScrollPosition: Int) {
        this.profileId = profileId
        _uiState.update { it.copy(scrollPosition = initialScrollPosition) }
    }

    fun loadBook(context: Context, book: Book) {
        _uiState.update {
            it.copy(
                isLoading = true,
                book = book,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                totalLines = loadBookLinesUseCase.countBookLines(book)
                val lines = loadBookLinesUseCase.loadBookLines(book, startLine = 0, linesToRead = linesPerLoad)
                loadedLines = lines.size

                val scrollPosition = bookViewModel.getScrollPosition(book.title)

                _uiState.update {
                    it.copy(
                        lines = lines,
                        isLoading = false,
                        currentPage = 1,
                        totalPages = calculateTotalPages(totalLines),
                        progress = 0f,
                        progressPercent = 0,
                        scrollPosition = scrollPosition
                    )
                }
                hasRestoredScroll = false
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Ошибка загрузки книги")
                }
            }
        }
    }

    fun updateScroll(firstVisibleLine: Int, offset: Int) {
        val book = _uiState.value.book ?: return
        if (!hasRestoredScroll) {
            return
        }
        viewModelScope.launch {
            val progress = if (totalLines > 0) {
                (firstVisibleLine.toFloat() / totalLines).coerceIn(0f, 1f)
            } else 0f
            val progressPercent = (progress * 100).toInt().coerceIn(0, 100)

            _uiState.update {
                it.copy(
                    progress = progress,
                    currentPage = calculateCurrentPage(firstVisibleLine),
                    progressPercent = progressPercent,
                    scrollPosition = firstVisibleLine,
                    scrollOffset = offset
                )
            }

            bookViewModel.saveScrollPosition(book.title, firstVisibleLine)

            try {
                saveProgressUseCase.saveProgress(
                    profileId = profileId,
                    book = book,
                    progressPercent = progressPercent.toFloat()
                )
            } catch (_: Exception) {}
        }
    }

    fun onScrollRestored() {
        hasRestoredScroll = true
    }

    private fun calculateTotalPages(totalLines: Int): Int {
        return (totalLines + linesPerLoad - 1) / linesPerLoad
    }

    private fun calculateCurrentPage(firstVisibleLine: Int): Int {
        return (firstVisibleLine / linesPerLoad) + 1
    }
}