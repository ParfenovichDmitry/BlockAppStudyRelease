package pl.parfen.blockappstudyrelease.ui.preview

import pl.parfen.blockappstudyrelease.data.model.Book

data class BookPreviewState(
    val isLoading: Boolean = false,
    val lines: List<String> = emptyList(),
    val book: Book? = null,
    val error: String? = null,
    val progress: Float = 0f,
    val progressPercent: Int = 0,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val profileId: Int = -1,
    val scrollPosition: Int = 0,
    val scrollOffset: Int = 0 // 👈 добавлено
)

