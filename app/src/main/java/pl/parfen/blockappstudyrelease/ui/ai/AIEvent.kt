package pl.parfen.blockappstudyrelease.ui.ai

sealed class AIEvent {
    data class SubmitPrompt(
        val model: String,
        val store: Boolean,
        val age: String,
        val selectedTopics: List<String>,
        val breakIntoSyllables: Boolean,
        val useOnlySecondLanguage: Boolean,
        val additionalLanguage: String?
    ) : AIEvent()
}
