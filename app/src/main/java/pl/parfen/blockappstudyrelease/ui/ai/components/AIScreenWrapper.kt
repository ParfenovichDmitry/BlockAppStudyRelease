package pl.parfen.blockappstudyrelease.ui.ai.components

import androidx.compose.runtime.Composable

@Composable
fun AIScreenWrapper(
    profileId: Int,
    age: String,
    appLanguage: String,
    additionalLanguage: String?,
    aiNetwork: String,
    aiTopics: List<String>,
    aiLanguage: String,
    selectedTopics: List<String> = emptyList(),
    onTopicsUpdated: (List<String>, List<String>) -> Unit = { _, _ -> },
    onEditTopics: () -> Unit = {}
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
