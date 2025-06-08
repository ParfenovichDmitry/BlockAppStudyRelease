package pl.parfen.blockappstudyrelease.viewmodel.blockapp

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.parfen.blockappstudyrelease.domain.usecase.GetTextForReadingUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.ValidatePasswordUseCase

class BlockedAppViewModelFactory(
    private val application: Application,
    private val blockedAppName: String,
    private val profileId: Int,
    private val fileUri: Uri?,
    private val getTextForReadingUseCase: GetTextForReadingUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val requiredCompliance: Float,
    private val selectedBookTitle: String?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlockedAppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlockedAppViewModel(
                application = application,
                blockedAppName = blockedAppName,
                profileId = profileId,
                fileUri = fileUri,
                getTextForReadingUseCase = getTextForReadingUseCase,
                validatePasswordUseCase = validatePasswordUseCase,
                requiredCompliance = requiredCompliance,
                selectedBookTitle = selectedBookTitle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
