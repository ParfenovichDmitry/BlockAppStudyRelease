package pl.parfen.blockappstudyrelease.ui.options.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.ui.theme.White

@Composable
fun OptionButton(
    imageRes: Int,
    textResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = 80.dp
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(140.dp)
            .shadow(if (isPressed) 0.dp else 8.dp, shape = RoundedCornerShape(20.dp))
            .background(White.copy(alpha = 0.8f), shape = RoundedCornerShape(20.dp))
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = stringResource(id = textResId),
                modifier = Modifier.size(imageSize)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = textResId),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
