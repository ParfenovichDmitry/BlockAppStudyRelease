package pl.parfen.blockappstudyrelease

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.data.local.db.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Profile
import pl.parfen.blockappstudyrelease.service.AppMonitoringService
import pl.parfen.blockappstudyrelease.ui.screens.ProfilesScreen
import androidx.core.content.edit

class ProfilesActivity : BaseActivity() {

    private lateinit var db: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var activeProfileId = -1
    private var profiles: SnapshotStateList<Profile> = mutableStateListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(this)
        sharedPreferences = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)

        setContent {
            ProfilesScreen(
                profiles = profiles,
                activeProfileId = activeProfileId,
                onAddProfile = { openCreateProfile() },
                onEditProfile = { openEditProfile(it) },
                onConnectProfile = { toggleProfile(it) },
                onDeleteProfile = { deleteProfile(it) },
                onBack = { finish() }
            )
        }

        loadProfiles()
        checkActiveProfile()
    }

    private fun loadProfiles() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updatedProfiles = db.profileDao().getAllProfiles()
                withContext(Dispatchers.Main) {
                    profiles.clear()
                    profiles.addAll(updatedProfiles)
                    Log.d("Database", "Profiles loaded: ${profiles.size}")
                }
            } catch (e: Exception) {
                Log.e("DatabaseError", "Error loading profiles: ${e.message}")
            }
        }
    }

    private fun checkActiveProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            activeProfileId = sharedPreferences.getInt("ACTIVE_PROFILE_ID", -1)
        }
    }

    private fun toggleProfile(profile: Profile) {
        if (activeProfileId != profile.id) {
            startService(profile.id)
            saveActiveProfileId(profile.id)
            activeProfileId = profile.id
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
                loadProfiles()
            } catch (e: Exception) {
                Log.e("DatabaseError", "Error deleting profile: ${e.message}")
            }
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
        sharedPreferences.edit() { putInt("ACTIVE_PROFILE_ID", profileId) }
    }

    private fun clearActiveProfileId() {
        sharedPreferences.edit() { remove("ACTIVE_PROFILE_ID") }
    }

    private fun startService(profileId: Int) {
        val intent = Intent(this, AppMonitoringService::class.java)
        intent.putExtra("ACTIVE_PROFILE_ID", profileId)
        startService(intent)
    }

    private fun stopService() {
        val intent = Intent(this, AppMonitoringService::class.java)
        stopService(intent)
    }
}
