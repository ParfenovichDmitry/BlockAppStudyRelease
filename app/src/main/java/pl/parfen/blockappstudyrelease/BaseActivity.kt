package pl.parfen.blockappstudyrelease

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import java.util.Locale

open class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .getString("selected_language", "en") ?: "en"
        val locale = Locale(savedLanguage)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val updatedContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(updatedContext)
    }
}