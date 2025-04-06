package pl.parfen.blockappstudyrelease

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

import pl.parfen.blockappstudyrelease.ui.main.LoadingScreen
import pl.parfen.blockappstudyrelease.ui.theme.BlockAppStudyReleaseTheme
import pl.parfen.blockappstudyrelease.utils.PermissionChecker

class MainActivity : BaseActivity() {

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val ENCRYPTED_PASSWORD_KEY = "encrypted_password"
    }

    private val usageStatsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (hasAllPermissions()) navigateNext()
    }

    private val overlayLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (hasAllPermissions()) navigateNext()
    }

    private val micPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && hasAllPermissions()) navigateNext()
    }

    private var checkPermissionsRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BlockAppStudyReleaseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainContent()
                }
            }
        }
    }

    @Composable
    private fun MainContent() {
        var isChecking by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            delay(300)
            checkPermissionsAndNavigate {
                isChecking = false
            }
        }

        if (isChecking) {
            LoadingScreen(text = getString(R.string.checking_permissions))
        } else {
            Box(modifier = Modifier.fillMaxSize())
        }
    }

    private fun checkPermissionsAndNavigate(onChecked: () -> Unit) {
        if (!checkPermissionsRequested) {
            checkPermissionsRequested = true

            if (hasAllPermissions()) {
                navigateNext()
            } else {
                requestPermissions()
            }
        }
        onChecked()
    }

    private fun requestPermissions() {
        if (!PermissionChecker.hasUsageStatsPermission(this)) {
            usageStatsLauncher.launch(PermissionChecker.getUsageStatsIntent())
        }
        if (!PermissionChecker.isOverlayPermissionGranted(this)) {
            overlayLauncher.launch(PermissionChecker.getOverlayIntent(this))
        }
        if (!PermissionChecker.isMicrophonePermissionGranted(this)) {
            micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun hasAllPermissions(): Boolean =
        PermissionChecker.hasUsageStatsPermission(this) &&
                PermissionChecker.isOverlayPermissionGranted(this) &&
                PermissionChecker.isMicrophonePermissionGranted(this)

    private fun navigateNext() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encryptedPassword = prefs.getString(ENCRYPTED_PASSWORD_KEY, null)

        val nextActivity = if (encryptedPassword.isNullOrEmpty()) {
            CreatePasswordActivity::class.java
        } else {
            PasswordLoginActivity::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }
}
