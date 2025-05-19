package pl.parfen.blockappstudyrelease.ui.ai

data class AIUiState(
    val isLoading: Boolean = false,
    val responseText: String = "",
    val errorMessage: String? = null,
    val age: String = "6",
    val selectedTopics: List<String> = emptyList(),
    val breakIntoSyllables: Boolean = false,
    val useOnlySecondLanguage: Boolean = false
)
