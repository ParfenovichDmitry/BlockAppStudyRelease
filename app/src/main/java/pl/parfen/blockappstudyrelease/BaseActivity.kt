package pl.parfen.blockappstudyrelease

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        var selectedLanguage = prefs.getString("selected_language", null)

        if (selectedLanguage == null) {
            // fallback: взять язык системы и сохранить
            selectedLanguage = Locale.getDefault().language.ifBlank { "pl" }
            prefs.edit().putString("selected_language", selectedLanguage).apply()
        }

        val locale = Locale(selectedLanguage)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val updatedContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(updatedContext)
    }

    fun getSystemLanguage(): String {
        val savedLanguage = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .getString("selected_language", null)
        return savedLanguage ?: Locale.getDefault().language.ifBlank { "pl" }
    }
}
