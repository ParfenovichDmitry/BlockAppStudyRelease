package pl.parfen.blockappstudyrelease.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.data.database.ProfileDao

import pl.parfen.blockappstudyrelease.data.model.Profile


class ProfilesViewModel(
    private val profileDao: ProfileDao
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            _profiles.value = profileDao.getAllProfiles()
        }
    }

    suspend fun deleteProfile(profile: Profile) {
        profileDao.delete(profile)
        loadProfiles()
    }
}
