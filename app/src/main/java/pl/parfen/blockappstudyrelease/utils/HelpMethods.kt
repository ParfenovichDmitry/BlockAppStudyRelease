
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object HelpMethods {


    fun getSystemLanguage(): String {
        return Locale.getDefault().language
    }

    fun updateContextLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}