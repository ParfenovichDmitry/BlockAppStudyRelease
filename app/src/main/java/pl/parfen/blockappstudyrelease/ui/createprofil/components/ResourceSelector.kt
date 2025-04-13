package pl.parfen.blockappstudyrelease.ui.createprofil.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R

@Composable
fun ResourceSelector(
    selectedResource: String,
    isAgeValid: Boolean,
    onResourceSelected: (String) -> Unit,
    onBookClick: () -> Unit,
    onAIClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.resource_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Кнопки "Książka" и "SI"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val isBookPressed = remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { isBookPressed.value = true },
                            onTap = {
                                isBookPressed.value = false
                                if (isAgeValid) {
                                    onResourceSelected("book")
                                    onBookClick()
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = if (isBookPressed.value) R.drawable.yes_press else R.drawable.yes_green),
                    contentDescription = stringResource(R.string.book_label),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text = stringResource(R.string.book_label),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            val isAIPressed = remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { isAIPressed.value = true },
                            onTap = {
                                isAIPressed.value = false
                                if (isAgeValid) {
                                    onResourceSelected("book")
                                    onAIClick()
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = if (isAIPressed.value) R.drawable.yes_press else R.drawable.yes_green),
                    contentDescription = stringResource(R.string.ai_label),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text = stringResource(R.string.ai_label),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Радиокнопки (активны, без подписей)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            RadioButton(
                selected = selectedResource == "book",
                onClick = { if (isAgeValid) onResourceSelected("book") },
                enabled = isAgeValid,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.Gray
                ),
                modifier = Modifier.weight(1f)
            )

            RadioButton(
                selected = selectedResource == "ai",
                onClick = { if (isAgeValid) onResourceSelected("ai") },
                enabled = isAgeValid,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.Gray
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}