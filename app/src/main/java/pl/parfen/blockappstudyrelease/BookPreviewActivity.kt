package pl.parfen.blockappstudyrelease

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.ViewModelProvider
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.ui.preview.BookPreviewScreen
import pl.parfen.blockappstudyrelease.viewmodel.BookPreviewViewModel
import pl.parfen.blockappstudyrelease.viewmodel.BookPreviewViewModelFactory

class BookPreviewActivity : BaseActivity() {  // <-- правильная основа

    private lateinit var viewModel: BookPreviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val book = intent.getParcelableExtra<Book>("book")
            ?: throw IllegalArgumentException("Book data missing in intent extras")

        viewModel = ViewModelProvider(
            this,
            BookPreviewViewModelFactory(applicationContext)
        )[BookPreviewViewModel::class.java]

        setContent {
            val listState = rememberLazyListState()

            BookPreviewScreen(
                viewModel = viewModel,
                book = book,           // <-- исправил initialBook -> book
                listState = listState
            )
        }
    }
}

