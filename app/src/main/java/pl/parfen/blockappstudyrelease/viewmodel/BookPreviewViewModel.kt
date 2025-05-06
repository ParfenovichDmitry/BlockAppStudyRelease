package pl.parfen.blockappstudyrelease.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.domain.usecase.GetNextBookUseCase
import pl.parfen.blockappstudyrelease.domain.LoadBookLinesUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.SaveProgressUseCase
import pl.parfen.blockappstudyrelease.ui.preview.BookPreviewState

class BookPreviewViewModel(
    private val loadBookLinesUseCase: LoadBookLinesUseCase,
    private val saveProgressUseCase: SaveProgressUseCase,
    private val getNextBookUseCase: GetNextBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookPreviewState())
    val uiState: StateFlow<BookPreviewState> = _uiState

    private val linesPerLoad = 500
    private var totalLines = 0
    private var loadedLines = 0
    private var profileId: Int = -1 // Теперь profileId хранится здесь!

    fun init(profileId: Int) {
        this.profileId = profileId
    }

    fun loadBook(book: Book) {
        _uiState.value = _uiState.value.copy(isLoading = true, book = book)
        viewModelScope.launch {
            try {
                totalLines = loadBookLinesUseCase.countBookLines(book)
                val lines = loadBookLinesUseCase.loadBookLines(book, startLine = 0, linesToRead = linesPerLoad)
                loadedLines = lines.size

                _uiState.value = _uiState.value.copy(
                    lines = lines,
                    isLoading = false,
                    currentPage = 1,
                    totalPages = calculateTotalPages(totalLines)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun loadMoreLines() {
        val book = _uiState.value.book ?: return
        viewModelScope.launch {
            try {
                val lines = loadBookLinesUseCase.loadBookLines(book, startLine = loadedLines, linesToRead = linesPerLoad)
                loadedLines += lines.size
                _uiState.value = _uiState.value.copy(
                    lines = _uiState.value.lines + lines,
                    totalPages = calculateTotalPages(totalLines)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateScroll(profileId: Int, firstVisibleLine: Int) {
        val book = _uiState.value.book ?: return
        viewModelScope.launch {
            val progress = if (totalLines > 0) {
                (firstVisibleLine.toFloat() / totalLines).coerceIn(0f, 1f)
            } else {
                0f
            }
            val progressPercent = (progress * 100).coerceIn(0f, 100f)

            _uiState.value = _uiState.value.copy(
                progress = progress,
                currentPage = calculateCurrentPage(firstVisibleLine),
                progressPercent = progressPercent.toInt()
            )

            saveProgressUseCase.saveProgress(
                profileId = profileId,
                book = book,
                progressPercent = progressPercent // <--- вот правильно
            )
        }
    }

    fun onCompleteReading() {
        val book = _uiState.value.book ?: return
        viewModelScope.launch {
            try {
                val nextBook = getNextBookUseCase.getNextBook(book)
                if (nextBook != null) {
                    loadBook(nextBook)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    private fun calculateTotalPages(totalLines: Int): Int {
        return (totalLines + linesPerLoad - 1) / linesPerLoad
    }

    private fun calculateCurrentPage(firstVisibleLine: Int): Int {
        return (firstVisibleLine / linesPerLoad) + 1
    }
}
