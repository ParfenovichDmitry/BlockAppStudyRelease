package pl.parfen.blockappstudyrelease.viewmodel.blockapp

data class BlockedAppUiState(
    val blockedAppName: String = "",
    val textToRead: String? = null,
    val progressPercent: Float = 0f,
    val isReading: Boolean = false,
    val isLoading: Boolean = true,
    val isTextLoaded: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val passwordError: String? = null
)
