package pl.parfen.blockappstudyrelease.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import pl.parfen.blockappstudyrelease.data.database.AppDatabase

class ProfilesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfilesViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            return ProfilesViewModel(database.profileDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
