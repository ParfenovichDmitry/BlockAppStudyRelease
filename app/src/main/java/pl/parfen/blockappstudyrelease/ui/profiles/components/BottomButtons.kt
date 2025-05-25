package pl.parfen.blockappstudyrelease.ui.profiles.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.model.Profile
import pl.parfen.blockappstudyrelease.ui.components.ImageButtonProf

@Composable
fun BottomButtons(
    selectedProfile: Profile?,
    activeProfileId: Int,
    onConnectProfile: () -> Unit,
    onEditProfile: (Profile) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isEditPressed by remember { mutableStateOf(false) }
    val isProfileActive = selectedProfile != null && selectedProfile.id == activeProfileId

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConnectProfileButton(
                isProfileActive = isProfileActive,
                onClick = { if (selectedProfile != null) onConnectProfile() },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ImageButtonProf(
                    text = stringResource(R.string.edit),
                    normalImageRes = R.drawable.yes_green,
                    pressedImageRes = R.drawable.yes_press,
                    onClick = {
                        selectedProfile?.let {
                            isEditPressed = true
                            coroutineScope.launch {
                                delay(200)
                                isEditPressed = false
                                onEditProfile(it)
                            }
                        }
                    },
                    textColor = Color.White,
                    singleLine = false,
                    modifier = Modifier
                        .widthIn(min = 140.dp, max = 180.dp)
                        .height(60.dp)
                )

                ImageButtonProf(
                    text = stringResource(R.string.back),
                    normalImageRes = R.drawable.no_red,
                    pressedImageRes = R.drawable.no_pres,
                    onClick = onBack,
                    textColor = Color.White,
                    singleLine = false,
                    modifier = Modifier
                        .widthIn(min = 140.dp, max = 180.dp)
                        .height(60.dp)
                )
            }
        }
    }
}
