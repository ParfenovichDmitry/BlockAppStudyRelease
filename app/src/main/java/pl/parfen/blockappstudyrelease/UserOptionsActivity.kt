package pl.parfen.blockappstudyrelease

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import pl.parfen.blockappstudyrelease.ui.options.UserOptionsScreen

class UserOptionsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserOptionsScreen(
                onProfilesClick = {
                    startActivity(Intent(this, ProfilesActivity::class.java))
                },
                onStatisticsClick = {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                },
                onOptionsClick = {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            )
        }
    }
}
