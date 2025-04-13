package pl.parfen.blockappstudyrelease.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.model.Profile
import pl.parfen.blockappstudyrelease.ui.components.AddProfileButton
import pl.parfen.blockappstudyrelease.ui.components.ProfileItem
import pl.parfen.blockappstudyrelease.ui.profiles.components.BottomButtons
import pl.parfen.blockappstudyrelease.ui.theme.TitleTextColor


@Composable
fun ProfilesScreen(
    profiles: List<Profile>,
    activeProfileId: Int,
    onAddProfile: () -> Unit,
    onEditProfile: (Profile) -> Unit,
    onConnectProfile: (Profile) -> Unit,
    onDeleteProfile: (Profile) -> Unit,
    onBack: () -> Unit
) {
    var selectedProfile by remember { mutableStateOf<Profile?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFB2DDB5), Color(0xFF85C1A6))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.profile_list),
                fontSize = 20.sp,
                color = TitleTextColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            AddProfileButton(onClick = onAddProfile)

            Spacer(modifier = Modifier.height(10.dp))

            if (profiles.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_profiles),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(profiles) { profile ->
                        ProfileItem(
                            profile = profile,
                            isSelected = profile.id == selectedProfile?.id,
                            onClick = { selectedProfile = profile },
                            onDeleteConfirmed = { onDeleteProfile(profile) }
                        )
                    }
                }
            }
        }

        BottomButtons(
            selectedProfile = selectedProfile,
            activeProfileId = activeProfileId,
            onConnectProfile = onConnectProfile,
            onEditProfile = onEditProfile,
            onBack = onBack
        )
    }
}
