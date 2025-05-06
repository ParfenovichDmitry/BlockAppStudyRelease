package pl.parfen.blockappstudyrelease.ui.createprofil

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import pl.parfen.blockappstudyrelease.ProfilesActivity
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.SelectAIActivity
import pl.parfen.blockappstudyrelease.SelectAppsActivity
import pl.parfen.blockappstudyrelease.SelectBookActivity


import pl.parfen.blockappstudyrelease.ui.components.AgeValidationAlert
import pl.parfen.blockappstudyrelease.ui.createprofil.components.*
import pl.parfen.blockappstudyrelease.ui.theme.GreenLight
import pl.parfen.blockappstudyrelease.ui.theme.GreenMedium
import pl.parfen.blockappstudyrelease.viewmodel.CreateProfileViewModel

private const val REQUEST_CODE_SELECT_APPS = 1001

@Composable
fun CreateProfileScreen(viewModel: CreateProfileViewModel) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState().value

    val selectAppsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedApps = result.data?.getStringArrayListExtra("selectedApps") ?: emptyList()
            viewModel.updateBlockedApps(selectedApps)
        }
    }

    val selectBookLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val books = result.data?.getStringArrayListExtra("books") ?: emptyList()
            val activeBook = result.data?.getStringExtra("selected_book") ?: ""
            viewModel.updateBooksAndActiveBook(books, activeBook)
        }
    }

    val selectAILauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val aiNetwork = result.data?.getStringExtra("aiNetwork") ?: "ChatGPT"
            val aiTopics = result.data?.getStringArrayListExtra("aiTopics") ?: emptyList()
            viewModel.updateAI(aiNetwork, aiTopics)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GreenLight, GreenMedium)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.inform),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            AvatarPicker(
                avatarUri = uiState.avatarUri,
                onAvatarSelected = { viewModel.updateAvatar(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileInputFields(
                nickname = uiState.nickname,
                age = uiState.age,
                onNicknameChange = { viewModel.updateNickname(it) },
                onAgeChange = { viewModel.updateAge(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResourceSelector(
                selectedResource = uiState.selectedResource,
                isAgeValid = uiState.isAgeValid(),
                onResourceSelected = { viewModel.updateSelectedResource(it) },
                onBookClick = {
                    if (viewModel.uiState.value.isAgeValid()) {
                        val intent = Intent(context, SelectBookActivity::class.java).apply {
                            putExtra("age", viewModel.uiState.value.age.toIntOrNull() ?: 0)
                        }
                        selectBookLauncher.launch(intent)
                    } else {
                        viewModel.showAgeValidationAlert()
                    }


        },
                onAIClick = {
                    if (uiState.isAgeValid()) {
                        val intent = Intent(context, SelectAIActivity::class.java).apply {
                            putExtra("age", uiState.age.toIntOrNull() ?: 0)
                        }
                        selectAILauncher.launch(intent)
                    } else {
                        viewModel.showAgeValidationAlert()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomSlidersSection(
                usageTime = uiState.usageTime,
                percentage = uiState.percentage,
                onUsageTimeChange = { viewModel.updateUsageTime(it) },
                onPercentageChange = { viewModel.updatePercentage(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BlockedAppsButton(
                onClick = {
                    if (uiState.isAgeValid()) {
                        val intent = Intent(context, SelectAppsActivity::class.java).apply {
                            putExtra("profile_id", viewModel.currentProfileId)
                            putStringArrayListExtra("blockedApps", ArrayList(viewModel.uiState.value.blockedApps))
                        }
                        selectAppsLauncher.launch(intent)
                    } else {
                        viewModel.showAgeValidationAlert()
                    }
                },
                isEnabled = uiState.isAgeValid()
            )




        }

        Spacer(modifier = Modifier.height(16.dp))

        SaveCancelButtons(
            isAgeValid = uiState.isAgeValid(),
            onSave = {
                if (uiState.isAgeValid()) {
                    viewModel.saveProfile(
                        onSuccess = {
                            val intent = Intent(context, ProfilesActivity::class.java)
                            context.startActivity(intent)
                            (context as? Activity)?.finish()
                        },
                        onError = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    viewModel.showAgeValidationAlert()
                }
            },
            onCancel = { (context as? Activity)?.finish() }
        )
    }

    if (uiState.showAgeValidationAlert) {
        AgeValidationAlert(
            onDismiss = { viewModel.dismissAgeValidationAlert() }
        )
    }
}
