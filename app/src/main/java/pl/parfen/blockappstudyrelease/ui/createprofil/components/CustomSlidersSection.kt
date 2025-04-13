package pl.parfen.blockappstudyrelease.ui.createprofil.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import kotlin.math.roundToInt
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlidersSection(
    usageTime: String,
    percentage: String,
    onUsageTimeChange: (String) -> Unit,
    onPercentageChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Время использования
        Column {
            Text(
                text = stringResource(R.string.usage_time_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = usageTime,
                    onValueChange = { input ->
                        val validated = validateUsageTime(input)
                        onUsageTimeChange(validated)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(80.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    singleLine = true
                )
                Slider(
                    value = usageTime.toFloatOrNull() ?: 1f,
                    onValueChange = { newValue ->
                        val rounded = newValue.roundToInt().toString()
                        onUsageTimeChange(validateUsageTime(rounded))
                    },
                    valueRange = 1f..120f,
                    // Убран steps, чтобы слайдер был непрерывным
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 0.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = Color.Transparent, // Прозрачный активный трек
                        inactiveTrackColor = Color.Gray.copy(alpha = 0.3f), // Серая шкала
                        activeTickColor = Color.Transparent, // Деления только на неактивной части
                        inactiveTickColor = Color.White // Белые деления
                    ),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF9BCB9D), CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .shadow(4.dp, CircleShape)
                        )
                    },
                    track = { sliderState ->
                        // Кастомный трек с делениями
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        ) {
                            // Добавляем деления вручную
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Деления для времени: 1, 30, 60, 90, 120
                                repeat(5) { index ->
                                    val position = when (index) {
                                        0 -> 1f
                                        1 -> 30f
                                        2 -> 60f
                                        3 -> 90f
                                        4 -> 120f
                                        else -> 1f
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(2.dp)
                                            .background(Color.White)
                                            .align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        // Процент
        Column {
            Text(
                text = stringResource(R.string.percentage_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = percentage,
                    onValueChange = { input ->
                        val validated = validatePercentage(input)
                        onPercentageChange(validated)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(80.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    singleLine = true
                )
                Slider(
                    value = percentage.toFloatOrNull() ?: 40f,
                    onValueChange = { newValue ->
                        val rounded = newValue.roundToInt().toString()
                        onPercentageChange(validatePercentage(rounded))
                    },
                    valueRange = 40f..100f,
                    // Убран steps, чтобы слайдер был непрерывным
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 0.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Gray.copy(alpha = 0.3f),
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.White
                    ),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF9BCB9D), CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .shadow(4.dp, CircleShape)
                        )
                    },
                    track = { sliderState ->
                        // Кастомный трек с делениями
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        ) {
                            // Добавляем деления вручную
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Деления для процента: 40, 60, 80, 100
                                repeat(4) { index ->
                                    val position = when (index) {
                                        0 -> 40f
                                        1 -> 60f
                                        2 -> 80f
                                        3 -> 100f
                                        else -> 40f
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(2.dp)
                                            .background(Color.White)
                                            .align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

// Валидация времени использования
private fun validateUsageTime(input: String): String {
    val value = input.toIntOrNull() ?: return "1"
    return value.coerceIn(1, 120).toString()
}

// Валидация процента
private fun validatePercentage(input: String): String {
    val value = input.toIntOrNull() ?: return "40"
    return value.coerceIn(40, 100).toString()
}