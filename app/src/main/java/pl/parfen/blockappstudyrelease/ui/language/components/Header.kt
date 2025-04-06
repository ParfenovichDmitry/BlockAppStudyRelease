package pl.parfen.blockappstudyrelease.ui.language.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.theme.HeaderGradientStart
import pl.parfen.blockappstudyrelease.ui.theme.HeaderGradientEnd
import pl.parfen.blockappstudyrelease.ui.theme.HeaderTextColor

@Composable
fun MultiLanguageHeader() {
    val scrollState = rememberInfiniteTransition(label = "Scroll")
    val offsetX by scrollState.animateFloat(
        initialValue = 0f,
        targetValue = -1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OffsetAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(HeaderGradientStart, HeaderGradientEnd)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "   ${stringResource(id = R.string.language_marquee)}   ".repeat(10),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = HeaderTextColor,
            modifier = Modifier.offset(x = offsetX.dp)
        )
    }
}
