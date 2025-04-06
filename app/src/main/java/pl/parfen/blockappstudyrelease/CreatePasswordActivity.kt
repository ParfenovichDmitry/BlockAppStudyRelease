package pl.parfen.blockappstudyrelease

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.data.PasswordRepository
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
                val encryptedPassword = PasswordEncryptor.encrypt(password)

                PasswordRepository.saveEncryptedPassword(
                    this@CreatePasswordActivity,
                    encryptedPassword
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

    private fun navigateToCheckQuestion() {
        val intent = Intent(this, CheckQuestion::class.java)
        startActivity(intent)
        finish()
    }
}
