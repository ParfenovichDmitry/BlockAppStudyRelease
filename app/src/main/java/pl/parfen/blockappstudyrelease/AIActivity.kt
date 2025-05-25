package pl.parfen.blockappstudyrelease

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import pl.parfen.blockappstudyrelease.ui.ai.AIScreen

class AIActivity : BaseActivity() {

    private val REQUEST_CODE_TOPIC_SELECTION = 1234
    private var selectedTopics: ArrayList<String> = arrayListOf()
    private var allTopics: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allTopics = getAllTopics()
        setContent { RenderScreen() }
    }

    private fun getAllTopics(): List<String> {
        val prefs = getSharedPreferences("user_topics", MODE_PRIVATE)
        val userTopics = prefs.getStringSet("user_topics", emptySet())?.toList() ?: emptyList()
        val defaultTopics = resources.getStringArray(R.array.default_topics).toList()
        return (defaultTopics + userTopics).distinct()
    }

    private fun getAppLanguage(): String {
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return prefs.getString("selected_language", "ru") ?: "ru"
    }

    @Composable
    private fun RenderScreen() {
        val appLanguage = getAppLanguage()
        AIScreen(
            profileId = intent.getIntExtra("profileId", -1),
            age = getAgeString(),
            appLanguage = appLanguage,
            additionalLanguage = intent.getStringExtra("additionalLanguage"),
            aiNetwork = intent.getStringExtra("aiNetwork") ?: "",
            aiTopics = allTopics,
            aiLanguage = appLanguage,
            selectedTopics = selectedTopics,
            onTopicsUpdated = { updatedTopics, selected ->
                selectedTopics = ArrayList(selected)
                allTopics = getAllTopics()
                setContent { RenderScreen() }
            },
            onEditTopics = {
                openTopicSelection()
            }
        )
    }

    private fun getAgeString(): String {

        val ageExtra = intent.getStringExtra("age")
        if (ageExtra != null) return ageExtra
        val ageInt = intent.getIntExtra("age", -1)
        return if (ageInt != -1) ageInt.toString() else ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TOPIC_SELECTION && resultCode == RESULT_OK) {
            val newTopics = data?.getStringArrayListExtra("currentTopics") ?: arrayListOf()
            allTopics = newTopics
            selectedTopics = ArrayList()
            setContent { RenderScreen() }
        }
    }

    fun openTopicSelection() {
        val intent = Intent(this, TopicSelectionActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_TOPIC_SELECTION)
    }
}
