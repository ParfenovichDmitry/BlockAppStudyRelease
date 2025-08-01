package pl.parfen.blockappstudyrelease

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.parfen.blockappstudyrelease.ui.books.BookScreen
import pl.parfen.blockappstudyrelease.ui.theme.BlockAppStudyReleaseTheme
import pl.parfen.blockappstudyrelease.viewmodel.BookViewModel
import pl.parfen.blockappstudyrelease.viewmodel.BookViewModelFactory

class SelectBookActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bookTitle = result.data?.getStringExtra("book_title").orEmpty()
                val scrollPosition = result.data?.getIntExtra("scroll_position", 0) ?: 0

                val prefs = getSharedPreferences("book_settings", MODE_PRIVATE)
                prefs.edit().putString("activeBook", bookTitle).putInt("scrollPosition", scrollPosition).apply()
            }
        }

        val profileId = intent.getIntExtra("profile_id", -1)
        val age = intent.getIntExtra("age", -1).takeIf { it != -1 }?.toString() ?: "?"
        val language = getSharedPreferences("app_settings", MODE_PRIVATE)
            .getString("selected_language", getSystemLocale()) ?: getSystemLocale()

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

    private fun getSystemLocale(): String {
        val locale = resources.configuration.locales.get(0)
        return locale.language
    }
}
