package pl.parfen.blockappstudyrelease.viewmodel


import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StartViewModel(private val prefs: SharedPreferences) : ViewModel() {

    private val _targetScreen = MutableStateFlow<TargetScreen?>(null)
    val targetScreen: StateFlow<TargetScreen?> = _targetScreen

    init {
        viewModelScope.launch {
            delay(4000)
            val selectedLanguage = prefs.getString("selected_language", null)
            _targetScreen.value = if (selectedLanguage != null) TargetScreen.MAIN else TargetScreen.LANGUAGE
        }
    }
}