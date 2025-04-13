package pl.parfen.blockappstudyrelease

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import pl.parfen.blockappstudyrelease.ui.selectapp.SelectAppScreen
import pl.parfen.blockappstudyrelease.ui.selectapps.SelectAppViewModel
import pl.parfen.blockappstudyrelease.ui.theme.BlockAppStudyReleaseTheme

class SelectAppsActivity : BaseActivity() {

    private val viewModel: SelectAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialBlockedApps = intent.getStringArrayListExtra("blockedApps")?.filter { it.isNotEmpty() } ?: emptyList()


        viewModel.setInitialBlockedApps(initialBlockedApps)

        setContent {
            BlockAppStudyReleaseTheme {
                SelectAppScreen(
                    viewModel = viewModel,
                    onSave = { selectedApps ->
                        val resultIntent = Intent().apply {
                            putStringArrayListExtra("selectedApps", ArrayList(selectedApps))
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}
