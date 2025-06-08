package pl.parfen.blockappstudyrelease.ui.blockapp

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun BlockedAppDialog(
    showDialog: Boolean,
    title: String,
    message: String,
    confirmText: String = "ОК",
    onConfirm: () -> Unit,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            if (dismissText != null && onDismiss != null) {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            }
        }
    )
}
