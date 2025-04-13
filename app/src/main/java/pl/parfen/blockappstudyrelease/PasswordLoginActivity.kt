package pl.parfen.blockappstudyrelease

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import pl.parfen.blockappstudyrelease.data.repository.PasswordRepository
import pl.parfen.blockappstudyrelease.security.PasswordEncryptor
import pl.parfen.blockappstudyrelease.ui.password.PasswordLoginScreen

class PasswordLoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasswordLoginScreen(
                onLogin = { password ->
                    startActivity(Intent(this, UserOptionsActivity::class.java))
                    finish()
                },
                onForgotPassword = {
                    startActivity(Intent(this, PasswordRecoveryActivity::class.java))
                },
                onCancel = {
                    finish()
                },
                checkPassword = { password ->
                    checkPassword(password)
                }
            )
        }
    }

    private fun checkPassword(inputPassword: String): Boolean {
        val encryptedPassword = PasswordRepository.getEncryptedPassword(this) ?: return false
        val decryptedPassword = PasswordEncryptor.decrypt(encryptedPassword) ?: return false
        return inputPassword == decryptedPassword
    }
}
