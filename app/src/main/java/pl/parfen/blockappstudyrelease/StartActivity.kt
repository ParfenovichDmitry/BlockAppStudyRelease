package pl.parfen.blockappstudyrelease

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import pl.parfen.blockappstudyrelease.ui.screens.WelcomeScreen
import pl.parfen.blockappstudyrelease.viewmodel.StartViewModel
import pl.parfen.blockappstudyrelease.viewmodel.StartViewModelFactory
import pl.parfen.blockappstudyrelease.viewmodel.TargetScreen

class StartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val viewModelFactory = StartViewModelFactory(prefs)
        val viewModel: StartViewModel by viewModels { viewModelFactory }

        setContent {
            val navTarget by viewModel.targetScreen.collectAsState()

            navTarget?.let {
                val intent = when (it) {
                    TargetScreen.MAIN -> Intent(this, MainActivity::class.java)
                    TargetScreen.LANGUAGE -> Intent(this, LanguageSelectionActivity::class.java)
                }
                startActivity(intent)
                finish()
            }

            MaterialTheme {
                WelcomeScreen()
            }
        }
    }
}

