package pl.parfen.blockappstudyrelease.ui.createprofil.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
fun SaveCancelButtons(
    isAgeValid: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопка "Zapisz" (зеленая)
        val isSavePressed = remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { isSavePressed.value = true },
                        onTap = {
                            isSavePressed.value = false
                            if (isAgeValid) onSave()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = if (isSavePressed.value) R.drawable.yes_press else R.drawable.yes_green),
                contentDescription = stringResource(R.string.save),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = stringResource(R.string.save),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Кнопка "Anuluj" (красная)
        val isCancelPressed = remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { isCancelPressed.value = true },
                        onTap = {
                            isCancelPressed.value = false
                            onCancel()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = if (isCancelPressed.value) R.drawable.no_pres else R.drawable.no_red),
                contentDescription = stringResource(R.string.cancel),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = stringResource(R.string.cancel),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}