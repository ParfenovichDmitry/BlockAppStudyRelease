package pl.parfen.blockappstudyrelease

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import pl.parfen.blockappstudyrelease.ui.topicselection.TopicSelectionScreen
import androidx.core.content.edit

class TopicSelectionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user_topics", MODE_PRIVATE)
        val userTopics = prefs.getStringSet("user_topics", emptySet())?.toList() ?: emptyList()
        val defaultTopics = resources.getStringArray(R.array.default_topics).toList()
        val allTopics = (defaultTopics + userTopics).distinct()

        setContent {
            TopicSelectionScreen(
                currentTopics = allTopics,
                onSave = { updatedTopics ->
                    val userOnly = updatedTopics.filter { it !in defaultTopics }
                    prefs.edit { putStringSet("user_topics", userOnly.toSet()) }
                    val resultIntent = Intent().apply {
                        putStringArrayListExtra(
                            "currentTopics",
                            ArrayList((defaultTopics + userOnly).distinct())
                        )
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                },
                onCancel = { finish() }
            )
        }
    }
}
