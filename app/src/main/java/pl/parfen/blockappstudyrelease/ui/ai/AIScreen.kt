package pl.parfen.blockappstudyrelease.ui.ai

import androidx.compose.runtime.Composable
import pl.parfen.blockappstudyrelease.ui.ai.components.AIStateHandler

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
    onTopicsUpdated: (List<String>, List<String>) -> Unit,
    onEditTopics: () -> Unit
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
        onTopicsUpdated = onTopicsUpdated,
        onEditTopics = onEditTopics
    )
}
