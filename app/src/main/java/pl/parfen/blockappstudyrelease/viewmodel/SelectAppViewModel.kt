package pl.parfen.blockappstudyrelease.ui.selectapps

import android.content.pm.ApplicationInfo
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.parfen.blockappstudyrelease.ui.selectapp.components.SelectAppUiState

class SelectAppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SelectAppUiState())
    val uiState: StateFlow<SelectAppUiState> = _uiState

    fun setAppList(apps: List<ApplicationInfo>) {
        _uiState.value = _uiState.value.copy(appList = apps)
    }

    fun toggleAppSelection(packageName: String) {
        val currentSelected = _uiState.value.selectedApps.toMutableSet()
        if (currentSelected.contains(packageName)) {
            currentSelected.remove(packageName)
        } else {
            currentSelected.add(packageName)
        }
        val isAllSelectedNow = currentSelected.size == _uiState.value.appList.size
        _uiState.value = _uiState.value.copy(
            selectedApps = currentSelected.toList(),
            isAllSelected = isAllSelectedNow
        )
    }

    fun toggleSelectAll() {
        val currentState = _uiState.value
        val allApps = currentState.appList.map { it.packageName }
        val isAllSelectedNow = !currentState.isAllSelected
        _uiState.value = currentState.copy(
            selectedApps = if (isAllSelectedNow) allApps else emptyList(),
            isAllSelected = isAllSelectedNow
        )
    }
    fun setInitialBlockedApps(initialBlockedApps: List<String>) {
        _uiState.value = _uiState.value.copy(selectedApps = initialBlockedApps)
    }

}

