package pl.parfen.blockappstudyrelease

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import pl.parfen.blockappstudyrelease.ui.createprofil.CreateProfileScreen

import pl.parfen.blockappstudyrelease.ui.theme.BlockAppStudyReleaseTheme
import pl.parfen.blockappstudyrelease.viewmodel.CreateProfileViewModel
import pl.parfen.blockappstudyrelease.viewmodel.CreateProfileViewModelFactory

class CreateProfileActivity : BaseActivity() {

    private val viewModel: CreateProfileViewModel by viewModels {
        CreateProfileViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val profileId = intent.getIntExtra("profile_id", -1)
        viewModel.initProfile(profileId)

        setContent {
            BlockAppStudyReleaseTheme {
                CreateProfileScreen(viewModel = viewModel)
            }
        }
    }
}
