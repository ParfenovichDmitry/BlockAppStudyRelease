package pl.parfen.blockappstudyrelease

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.parfen.blockappstudyrelease.ui.books.BookScreen
import pl.parfen.blockappstudyrelease.ui.theme.BlockAppStudyReleaseTheme
import pl.parfen.blockappstudyrelease.viewmodel.BookViewModel
import pl.parfen.blockappstudyrelease.viewmodel.BookViewModelFactory

class SelectBookActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val profileId = intent.getIntExtra("profile_id", -1)
        val age = intent.getIntExtra("age", -1).takeIf { it != -1 }?.toString() ?: "?"

        val language = getSharedPreferences("app_settings", MODE_PRIVATE)
            .getString("selected_language", "en") ?: "en"

        setContent {
            BlockAppStudyReleaseTheme {
                val bookViewModel: BookViewModel = viewModel(
                    factory = BookViewModelFactory(applicationContext, this)
                )

                BookScreen(
                    viewModel = bookViewModel,
                    profileId = profileId,
                    age = age,
                    primaryLanguage = language,
                    onSave = { updatedShowAllBooks, updatedSecondaryLanguage ->
                        val resultIntent = Intent().apply {
                            putExtra("showAllBooks", updatedShowAllBooks)
                            putExtra("additionalLanguage", updatedSecondaryLanguage)
                            putExtra("selectedBookTitle", bookViewModel.uiState.value.selectedBookTitle)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    },
                    onCancel = {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}
