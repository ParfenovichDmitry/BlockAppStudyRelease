package pl.parfen.blockappstudyrelease.ui.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.security.PasswordEncryptor

class PasswordRecoveryViewModel : ViewModel() {

    private val _decryptedPassword = MutableStateFlow<String?>(null)
    val decryptedPassword = _decryptedPassword.asStateFlow()

    private companion object {
        private const val IV_SIZE = 16
        private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    }

    fun decryptPassword(
        encryptedPassword: String,
        userAnswer: String,
        savedAnswer: String,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            if (isAnswerCorrect(userAnswer, savedAnswer)) {
                delay(500)
                try {
                    val decrypted = PasswordEncryptor.decrypt(encryptedPassword)
                    if (decrypted != null) {
                        _decryptedPassword.value = decrypted
                    } else {
                        _decryptedPassword.value = null
                        onError()
                    }
                } catch (e: Exception) {

                    _decryptedPassword.value = null
                    onError()
                }
            } else {
                _decryptedPassword.value = null
                onError()
            }
        }
    }

    private fun isAnswerCorrect(userAnswer: String, savedAnswer: String?): Boolean {
        return userAnswer.trim().equals(savedAnswer?.trim(), ignoreCase = true)
    }
}
