package pl.parfen.blockappstudyrelease.ui.ai

import androidx.compose.runtime.Composable
import pl.parfen.blockappstudyrelease.ui.ai.components.AIStateHandler
import pl.parfen.blockappstudyrelease.ui.ai.components.Topic

@Composable
fun AIScreen(
    profileId: Int,
    age: String,
    appLanguage: String,
    additionalLanguage: String?,
    aiNetwork: String,
    aiTopics: List<String>,
    aiLanguage: String,
    selectedTopics: List<String>,
    onTopicsUpdated: (List<Topic>, List<String>) -> Unit

) {
    AIStateHandler(
        profileId = profileId,
        age = age,
        appLanguage = appLanguage,
        additionalLanguage = additionalLanguage,
        aiNetwork = aiNetwork,
        aiTopics = aiTopics,
        aiLanguage = aiLanguage,
        selectedTopics = selectedTopics,
        onTopicsUpdated = onTopicsUpdated
    )
}
