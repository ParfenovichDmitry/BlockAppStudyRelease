package pl.parfen.blockappstudyrelease

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.data.database.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Profile
import pl.parfen.blockappstudyrelease.blockservice.AppMonitoringService
import pl.parfen.blockappstudyrelease.ui.screens.ProfilesScreen
import androidx.core.content.edit

class ProfilesActivity : BaseActivity() {

    private lateinit var db: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var profiles: SnapshotStateList<Profile> = mutableStateListOf()
    private var selectedProfile by mutableStateOf<Profile?>(null)
    private var activeProfileId by mutableStateOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(this)
        sharedPreferences = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)

        setContent {
            ProfilesScreen(
                profiles = profiles,
                activeProfileId = activeProfileId,
                selectedProfile = selectedProfile,
                onSelectProfile = { selectedProfile = it },
                onAddProfile = { openCreateProfile() },
                onEditProfile = { openEditProfile(it) },
                onConnectProfile = { handleConnectProfile() },
                onDeleteProfile = { deleteProfile(it) },
                onBack = { finish() }
            )
        }

        loadProfiles()
        updateActiveProfileId()
    }

    private fun loadProfiles() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updatedProfiles = db.profileDao().getAllProfiles()
                withContext(Dispatchers.Main) {
                    profiles.clear()
                    profiles.addAll(updatedProfiles)
                }
            } catch (_: Exception) { }
        }
    }

    private fun updateActiveProfileId() {
        activeProfileId = sharedPreferences.getInt("ACTIVE_PROFILE_ID", -1)
    }

    // Проверка разрешений UsageStats и Overlay
    private fun hasPermissions(): Boolean {
        val usage = try {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (_: Exception) { false }
        val overlay = Settings.canDrawOverlays(this)
        return usage && overlay
    }

    // Включение/отключение профиля
    private fun handleConnectProfile() {
        val profile = selectedProfile ?: return
        if (!hasPermissions()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            return
        }
        if (activeProfileId != profile.id) {
            saveActiveProfileId(profile.id)
            activeProfileId = profile.id
            startServiceForProfile(profile.id)
            moveTaskToBack(true)
        } else {
            stopService()
            clearActiveProfileId()
            activeProfileId = -1
        }
        loadProfiles()
    }

    private fun deleteProfile(profile: Profile) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.profileDao().delete(profile)
                if (activeProfileId == profile.id) {
                    stopService()
                    clearActiveProfileId()
                    activeProfileId = -1
                }
                withContext(Dispatchers.Main) {
                    if (selectedProfile?.id == profile.id) selectedProfile = null
                    loadProfiles()
                }
            } catch (_: Exception) { }
        }
    }

    private fun openCreateProfile() {
        startActivity(Intent(this, CreateProfileActivity::class.java))
    }

    private fun openEditProfile(profile: Profile) {
        val intent = Intent(this, CreateProfileActivity::class.java).apply {
            putExtra("profile_id", profile.id)
        }
        startActivity(intent)
    }

    private fun saveActiveProfileId(profileId: Int) {
        sharedPreferences.edit { putInt("ACTIVE_PROFILE_ID", profileId) }
    }

    private fun clearActiveProfileId() {
        sharedPreferences.edit { remove("ACTIVE_PROFILE_ID") }
    }

    private fun startServiceForProfile(profileId: Int) {
        val intent = Intent(this, AppMonitoringService::class.java)
        intent.putExtra("ACTIVE_PROFILE_ID", profileId)
        startForegroundService(intent)
    }

    private fun stopService() {
        val intent = Intent(this, AppMonitoringService::class.java)
        stopService(intent)
    }
}
