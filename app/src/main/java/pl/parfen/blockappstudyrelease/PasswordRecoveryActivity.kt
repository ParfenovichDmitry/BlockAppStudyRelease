package pl.parfen.blockappstudyrelease

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import pl.parfen.blockappstudyrelease.ui.password.PasswordRecoveryScreen

class PasswordRecoveryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasswordRecoveryScreen(
                onLogin = {
                    startActivity(Intent(this, PasswordLoginActivity::class.java))
                    finish()
                },
                onResetPassword = {
                    startActivity(Intent(this, CreatePasswordActivity::class.java))
                    finish()
                }
            )
        }
    }
}
