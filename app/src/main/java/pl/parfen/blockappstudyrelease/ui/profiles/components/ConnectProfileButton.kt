package pl.parfen.blockappstudyrelease.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R

@Composable
fun ConnectProfileButton(
    isProfileActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonText = if (isProfileActive) {
        stringResource(R.string.disconnect_profile)
    } else {
        stringResource(R.string.connect_profile)
    }

    val buttonImage = if (isProfileActive) {
        R.drawable.no_red // красная кнопка
    } else {
        R.drawable.yes_green // зелёная кнопка
    }

    val textColor = if (isProfileActive) {
        MaterialTheme.colorScheme.onPrimary // Белый на красном фоне
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    Box(
        modifier = modifier
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = buttonImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = buttonText,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
