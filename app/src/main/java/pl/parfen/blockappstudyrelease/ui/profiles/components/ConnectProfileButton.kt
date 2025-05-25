package pl.parfen.blockappstudyrelease.ui.profiles.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R

@Composable
fun ConnectProfileButton(
    isProfileActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonText = if (isProfileActive)
        stringResource(R.string.disconnect_profile)
    else
        stringResource(R.string.connect_profile)

    val buttonImage = if (isProfileActive)
        R.drawable.no_red
    else
        R.drawable.yes_green

    val textColor = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier.clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = buttonImage),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
        )
        Text(
            text = buttonText,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
