package pl.parfen.blockappstudyrelease.ui.profiles.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ImageButton(
    text: String,
    normalImageRes: Int,
    pressedImageRes: Int,
    isPressed: Boolean,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    Box(
        modifier = modifier
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = if (isPressed) pressedImageRes else normalImageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = text,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis,
            softWrap = true
        )
    }
}
