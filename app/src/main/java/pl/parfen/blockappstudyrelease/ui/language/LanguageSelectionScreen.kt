package pl.parfen.blockappstudyrelease.ui.language

import android.graphics.Paint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.theme.*
import kotlin.math.ceil

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String, Array<String>, Array<String>) -> Unit
) {
    val languages = stringArrayResource(R.array.available_languages)
    val languageCodes = stringArrayResource(R.array.language_codes)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GreenLight, GreenMedium)
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MultiLanguageHeader()
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(languages.size) { index ->
                FlagButton(
                    flagResId = getFlagResource(languageCodes[index]),
                    label = languages[index],
                    onClick = { onLanguageSelected(languages[index], languages, languageCodes) }
                )
            }
        }
    }
}

@Composable
private fun MultiLanguageHeader() {
    val fullText = stringResource(id = R.string.language_selection_title)
    val fontSizeSp = 18.sp
    val density = LocalDensity.current

    val textPaint = remember {
        Paint().apply {
            textSize = with(density) { fontSizeSp.toPx() }
        }
    }
    val singleLoopText = remember(fullText) { "$fullText    " }
    val textWidthPx = remember(singleLoopText, textPaint) {
        textPaint.measureText(singleLoopText)
    }
    val textWidthDp = with(density) { textWidthPx.toDp() }
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp

    val repeatCount = remember(screenWidthDp, textWidthDp) {
        if (textWidthDp > 0.dp) {
            ceil(screenWidthDp / textWidthDp).toInt() + 2
        } else {
            2
        }
    }
    val repeatedText = remember(repeatCount, singleLoopText) {
        singleLoopText.repeat(repeatCount)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ScrollTransition")
    val animationDuration = 15000

    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -textWidthPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OffsetXAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(GreenDarkStart, GreenDarkEnd)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .clipToBounds()
    ) {
        Text(
            text = repeatedText,
            fontSize = fontSizeSp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .wrapContentWidth(unbounded = true)
                .graphicsLayer { translationX = offsetX }
                .align(Alignment.CenterStart)
        )
    }
}

@Composable
private fun FlagButton(flagResId: Int, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = LanguageLabelText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .background(LanguageLabelBackground, RoundedCornerShape(4.dp))
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            )
        }
    }
}

private fun getFlagResource(languageCode: String): Int {
    return when (languageCode) {
        "pl" -> R.drawable.pol_flag
        "en" -> R.drawable.uk_flag
        "ru" -> R.drawable.rus_flag
        "de" -> R.drawable.german_flag
        "fr" -> R.drawable.fr_flag
        else -> R.drawable.uk_flag
    }
}
