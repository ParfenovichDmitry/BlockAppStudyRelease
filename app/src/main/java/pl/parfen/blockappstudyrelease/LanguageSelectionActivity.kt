package pl.parfen.blockappstudyrelease

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.ui.language.LanguageSelectionScreen
import java.util.Locale

class LanguageSelectionActivity : BaseActivity() {

    companion object {
        private const val TAG = "LanguageSelection"
        private const val PREFS_NAME = "app_settings"
        private const val PREF_KEY_LANGUAGE = "selected_language"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "LanguageSelectionActivity started")
        setContent {
            LanguageSelectionScreen { selectedLanguage, languages, languageCodes ->
                handleLanguageSelection(selectedLanguage, languages, languageCodes)
            }
        }
    }

    private fun handleLanguageSelection(
        selectedLanguage: String,
        languages: Array<String>,
        languageCodes: Array<String>
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val languageCode = getLanguageCode(selectedLanguage, languages, languageCodes)
            saveLanguagePreference(languageCode)

            launch(Dispatchers.Main) {
                Log.d(TAG, "Language selected: $languageCode")


                updateLocale(languageCode)


                val localizedContext = createLocalizedContext(languageCode)
                val localizedLanguages =
                    localizedContext.resources.getStringArray(R.array.available_languages)

                val localizedLanguageName =
                    getLocalizedLanguageName(languageCode, languageCodes, localizedLanguages)

                val toastMessage =
                    localizedContext.getString(R.string.language_selected, localizedLanguageName)
                showToast(toastMessage)

                navigateToMainActivity()
            }
        }
    }

    private fun getLanguageCode(
        language: String,
        languages: Array<String>,
        codes: Array<String>
    ): String {
        val index = languages.indexOf(language)
        return if (index != -1) codes[index] else "en"
    }

    private fun saveLanguagePreference(languageCode: String) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(PREF_KEY_LANGUAGE, languageCode)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainActivity() {
        Log.d(TAG, "Navigating to MainActivity")
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun createLocalizedContext(languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        return createConfigurationContext(config)
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun getLocalizedLanguageName(
        languageCode: String,
        codes: Array<String>,
        localizedLanguages: Array<String>
    ): String {
        val index = codes.indexOf(languageCode)
        return if (index != -1) localizedLanguages[index] else localizedLanguages.getOrNull(0)
            ?: "Unknown"
    }
}
