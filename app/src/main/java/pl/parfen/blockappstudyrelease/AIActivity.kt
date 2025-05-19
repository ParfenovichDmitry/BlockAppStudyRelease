package pl.parfen.blockappstudyrelease

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import pl.parfen.blockappstudyrelease.ui.ai.AIScreen
import pl.parfen.blockappstudyrelease.ui.ai.components.Topic
import java.util.*

class AIActivity : BaseActivity() {

    private lateinit var updateTopics: (List<Topic>, List<String>) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val savedLanguage = sharedPrefs.getString("selected_language", "en") ?: "en"

        val profileId = intent.getIntExtra("profile_id", -1)
        val age = intent.getIntExtra("age", 6).toString()
        val appLanguage = intent.getStringExtra("app_language") ?: savedLanguage
        val additionalLanguage = intent.getStringExtra("additionalLanguage")
        val aiNetwork = intent.getStringExtra("aiNetwork") ?: "ChatGPT"
        var initialTopics = intent.getStringArrayListExtra("aiTopics") ?: emptyList()
        val aiLanguage = intent.getStringExtra("aiLanguage") ?: appLanguage
        val initialSelectedTopics = intent.getStringArrayListExtra("selectedTopics") ?: emptyList()

        if (initialTopics.isEmpty()) {
            val locale = Locale(appLanguage)
            val config = resources.configuration
            config.setLocale(locale)
            val localizedContext = createConfigurationContext(config)
            val fallbackTopics = localizedContext.resources.getStringArray(R.array.default_topics).toList()
            initialTopics = fallbackTopics
        }

        setContent {
            updateTopics = { newTopics, newSelectedTopics ->

            }

            AIScreen(
                profileId = profileId,
                age = age,
                appLanguage = appLanguage,
                additionalLanguage = additionalLanguage,
                aiNetwork = aiNetwork,
                aiTopics = initialTopics,
                aiLanguage = aiLanguage,
                selectedTopics = initialSelectedTopics,
                onTopicsUpdated = updateTopics
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val updatedTopics = data?.getStringArrayListExtra("currentTopics") ?: emptyList()
            val selectedTopics = data?.getStringArrayListExtra("selectedTopics") ?: emptyList()

        }
    }
}
