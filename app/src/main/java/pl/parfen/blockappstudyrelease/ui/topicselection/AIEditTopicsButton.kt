import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R

@Composable
fun AIEditTopicsButton(
    onEditTopics: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val background: Painter = if (isPressed) {
        painterResource(id = R.drawable.yes_press)
    } else {
        painterResource(id = R.drawable.yes_green)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(vertical = 4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onEditTopics() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = background,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = stringResource(R.string.topics_button_label),
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
