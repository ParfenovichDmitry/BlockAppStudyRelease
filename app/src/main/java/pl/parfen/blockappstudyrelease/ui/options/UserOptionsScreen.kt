package pl.parfen.blockappstudyrelease.ui.options

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.theme.GreenLight
import pl.parfen.blockappstudyrelease.ui.theme.GreenMedium
import pl.parfen.blockappstudyrelease.ui.options.components.OptionButton

@Composable
fun UserOptionsScreen(
    onProfilesClick: () -> Unit,
    onOptionsClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(GreenLight, GreenMedium)))
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OptionButton(
            imageRes = R.drawable.profiles_icon,
            textResId = R.string.profiles,
            onClick = onProfilesClick
        )

        OptionButton(
            imageRes = R.drawable.options_icon,
            textResId = R.string.options,
            onClick = onOptionsClick
        )

        OptionButton(
            imageRes = R.drawable.statistic,
            textResId = R.string.statistics,
            onClick = onStatisticsClick
        )
    }
}
