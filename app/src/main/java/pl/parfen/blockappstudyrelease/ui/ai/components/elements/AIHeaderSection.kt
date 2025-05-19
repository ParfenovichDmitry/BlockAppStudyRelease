package pl.parfen.blockappstudyrelease.ui.ai.components.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.theme.SuccessGreen

@Composable
fun AIHeaderSection(
    age: String,
    languageNames: List<String>,
    selectedLanguageIndex: Int,
    selectedAdditionalLanguageIndex: Int,
    onLanguageChange: (Int) -> Unit,
    onAdditionalLanguageChange: (Int) -> Unit,
    useOnlySecondLanguage: Boolean,
    onUseOnlySecondLanguageChange: (Boolean) -> Unit,
    breakIntoSyllables: Boolean,
    onBreakIntoSyllablesChange: (Boolean) -> Unit,
    freeAttempts: Int,
    isSubscribed: Boolean,
    subscriptionEndDate: String,
    onSubscribe: () -> Unit,
    onResetAttempts: () -> Unit
) {
    val secondaryIndex = if (selectedAdditionalLanguageIndex == -1) 0 else selectedAdditionalLanguageIndex

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.language_label, languageNames[selectedLanguageIndex]),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.age_label2, age),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = useOnlySecondLanguage,
                    onCheckedChange = onUseOnlySecondLanguageChange
                )
                Text(
                    text = stringResource(R.string.additional_language_label),
                    fontSize = 14.sp
                )
            }

            LanguageDropdown(
                selectedIndex = secondaryIndex,
                options = languageNames,
                onSelectedIndexChange = onAdditionalLanguageChange
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = breakIntoSyllables,
                onCheckedChange = onBreakIntoSyllablesChange
            )
            Text(
                text = stringResource(R.string.break_into_syllables),
                fontSize = 14.sp
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (isSubscribed) {
                Text(
                    text = stringResource(R.string.subscription_until, subscriptionEndDate),
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = stringResource(R.string.free_attempts, freeAttempts),
                    fontSize = 14.sp
                )
                if (freeAttempts == 0) {
                    Button(
                        onClick = onSubscribe,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.subscribe_one_month))
                    }
                } else {
                    Text(
                        text = stringResource(R.string.long_press_reset),
                        fontSize = 12.sp,
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(onLongPress = { onResetAttempts() })
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageDropdown(
    selectedIndex: Int,
    options: List<String>,
    onSelectedIndexChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(160.dp)
            .clickable { expanded = true }
            .background(SuccessGreen)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = options.getOrElse(selectedIndex) { options.firstOrNull().orEmpty() },
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(160.dp)
        ) {
            options.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = if (index == selectedIndex) Color(0xFF1B5E20) else Color.Black,
                            fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelectedIndexChange(index)
                        expanded = false
                    }
                )
            }
        }
    }
}
