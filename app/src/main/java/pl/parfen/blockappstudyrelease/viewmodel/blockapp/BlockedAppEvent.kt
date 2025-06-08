package pl.parfen.blockappstudyrelease.viewmodel.blockapp

sealed class BlockedAppEvent {
    object LoadProfileData : BlockedAppEvent()
    object FetchTextForReading : BlockedAppEvent()
    object StartListening : BlockedAppEvent()
    object StopListening : BlockedAppEvent()
    data class UpdatePasswordInput(val value: String) : BlockedAppEvent()
    object SubmitPassword : BlockedAppEvent()
    object DismissPasswordDialog : BlockedAppEvent()
    object ShowPasswordDialog : BlockedAppEvent()
    data class ShowError(val message: String) : BlockedAppEvent()
    object DismissErrorDialog : BlockedAppEvent()
    data class OnRecognitionResult(val recognizedText: String) : BlockedAppEvent()
    data class OnRecognitionError(val errorMessage: String) : BlockedAppEvent()
    object UnlockApp : BlockedAppEvent()
}
