package pl.parfen.blockappstudyrelease.ui.createprofil.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import androidx.compose.ui.res.stringResource

@Composable
fun ProfileInputFields(
    nickname: String,
    age: String,
    onNicknameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = nickname,
                onValueChange = onNicknameChange,
                label = {
                    Text(
                        text = stringResource(R.string.nickname_label),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                singleLine = true,
                modifier = Modifier.weight(2f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                label = {
                    Text(
                        text = stringResource(R.string.age_label),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
