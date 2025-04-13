package pl.parfen.blockappstudyrelease.ui.selectapp.components

import android.content.pm.ApplicationInfo

data class SelectAppUiState(
    val appList: List<ApplicationInfo> = emptyList(),
    val selectedApps: List<String> = emptyList(),
    val isAllSelected: Boolean = false
)
