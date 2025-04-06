package pl.parfen.blockappstudyrelease

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.parfen.blockappstudyrelease.ui.checkquestion.CheckQuestionScreen
import pl.parfen.blockappstudyrelease.viewmodel.CheckQuestionViewModel

class CheckQuestion : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: CheckQuestionViewModel = viewModel()

            CheckQuestionScreen(
                viewModel = viewModel,
                onSaveSuccess = {
                    startActivity(Intent(this, PasswordLoginActivity::class.java))
                    finish()
                },
                onCancel = {
                    startActivity(Intent(this, CreatePasswordActivity::class.java))
                    finish()
                }
            )
        }
    }
}
