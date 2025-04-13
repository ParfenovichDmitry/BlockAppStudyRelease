package pl.parfen.blockappstudyrelease.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.parfen.blockappstudyrelease.R

@Composable
fun AgeValidationAlert(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.age_validation_title)) },
        text = { Text(text = stringResource(id = R.string.age_validation_message)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    )
}
