package pl.parfen.blockappstudyrelease.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.utils.PermissionManager

@Composable
fun PermissionScreen(
    onPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val usageStatsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    val overlayLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    val micPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    var checking by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(500)
        checking = false
    }

    if (checking) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (!PermissionManager.hasUsageStatsPermission(context)) {
                        usageStatsLauncher.launch(PermissionManager.getUsageStatsIntent(context))
                    }
                    if (!PermissionManager.hasOverlayPermission(context)) {
                        overlayLauncher.launch(PermissionManager.getOverlayPermissionIntent(context))
                    }
                    if (!PermissionManager.hasMicrophonePermission(context)) {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }

                    scope.launch {
                        delay(1000)
                        if (PermissionManager.hasUsageStatsPermission(context) &&
                            PermissionManager.hasOverlayPermission(context) &&
                            PermissionManager.hasMicrophonePermission(context)
                        ) {
                            onPermissionsGranted()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(id = R.string.grant_accesses),
                    fontSize = 18.sp
                )
            }
        }
    }
}
