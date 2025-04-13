package pl.parfen.blockappstudyrelease

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.data.repository.PasswordRepository
import pl.parfen.blockappstudyrelease.security.PasswordEncryptor
import pl.parfen.blockappstudyrelease.ui.password.CreatePasswordScreen

class CreatePasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CreatePasswordScreen(
                onSavePassword = { password ->
                    savePassword(password)
                },
                onCancel = {
                    finish()
                }
            )
        }
    }

    private fun savePassword(password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                PasswordEncryptor.createSecretKeyIfNeeded()

                val encryptedPassword = PasswordEncryptor.encrypt(password)

                PasswordRepository.saveEncryptedPassword(
                    this@CreatePasswordActivity,
                    encryptedPassword ?: throw IllegalStateException("Error encrypting password")
                )

                launch(Dispatchers.Main) {
                    navigateToCheckQuestion()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@CreatePasswordActivity,
                        getString(R.string.error_encrypt_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun showEncryptionError() {
        Toast.makeText(
            this,
            getString(R.string.error_encrypt_password),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateToCheckQuestion() {
        startActivity(Intent(this, CheckQuestion::class.java))
        finish()
    }
}
