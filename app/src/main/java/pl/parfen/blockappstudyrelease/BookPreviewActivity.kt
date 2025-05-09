package pl.parfen.blockappstudyrelease

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.core.os.BuildCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.repository.BookRepositoryImpl
import pl.parfen.blockappstudyrelease.domain.LoadBookLinesUseCase
import pl.parfen.blockappstudyrelease.domain.SaveProgressUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.GetNextBookUseCase
import pl.parfen.blockappstudyrelease.ui.preview.BookPreviewScreen
import pl.parfen.blockappstudyrelease.viewmodel.BookPreviewViewModel
import pl.parfen.blockappstudyrelease.viewmodel.BookPreviewViewModelFactory
import pl.parfen.blockappstudyrelease.viewmodel.BookViewModel

class BookPreviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val book = if (SDK_INT >= 33) {
            intent.getParcelableExtra("book", Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("book")
        }
        val scrollPosition = intent.getIntExtra("scrollPosition", 0)
        val profileId = intent.getIntExtra("profileId", -1)

        if (book == null) {
            finish()
            return
        }

        val bookViewModel = ViewModelProvider(
            this,
            viewModelFactory {
                initializer {
                    BookViewModel(applicationContext, createSavedStateHandle())
                }
            }
        ).get(BookViewModel::class.java)

        val bookRepository = BookRepositoryImpl(applicationContext)
        val loadBookLinesUseCase = LoadBookLinesUseCase(bookRepository)
        val saveProgressUseCase = SaveProgressUseCase(bookRepository)
        val getNextBookUseCase = GetNextBookUseCase(bookRepository)

        val bookPreviewViewModel = ViewModelProvider(
            this,
            BookPreviewViewModelFactory(
                loadBookLinesUseCase = loadBookLinesUseCase,
                saveProgressUseCase = saveProgressUseCase,
                getNextBookUseCase = getNextBookUseCase,
                bookViewModel = bookViewModel
            )
        ).get(BookPreviewViewModel::class.java)

        setContent {
            val listState = rememberLazyListState()
            BookPreviewScreen(
                viewModel = bookPreviewViewModel,
                book = book,
                listState = listState,
                profileId = profileId,
                initialScrollPosition = scrollPosition,
                bookViewModel = bookViewModel
            )
        }
    }
}