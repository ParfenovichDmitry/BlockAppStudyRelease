package pl.parfen.blockappstudyrelease.ui.blockapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.viewmodel.blockapp.BlockedAppUiState
import pl.parfen.blockappstudyrelease.ui.theme.GreenLight
import pl.parfen.blockappstudyrelease.ui.theme.GreenMedium
import pl.parfen.blockappstudyrelease.ui.theme.SuccessGreen
import pl.parfen.blockappstudyrelease.ui.theme.ErrorRed

@Composable
fun BlockedAppScreen(
    uiState: BlockedAppUiState,
    onStartReading: () -> Unit,
    onStopReading: () -> Unit,
    onShowPasswordDialog: () -> Unit,
    onPasswordEntered: (String) -> Unit,
    onUnlock: () -> Unit,
    onCancelPasswordDialog: () -> Unit
) {
    val scrollState = rememberScrollState()
    var passwordInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GreenLight, GreenMedium)
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Для разблокировки прочитай вслух:",
                fontSize = 19.sp,
                color = SuccessGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.blockedAppName,
                fontSize = 16.sp,
                color = ErrorRed
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Text(
                    text = uiState.textToRead ?: "",
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(bottom = 12.dp)
                )
            }

            LinearProgressIndicator(
                progress = uiState.progressPercent / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { if (uiState.isReading) onStopReading() else onStartReading() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isReading) ErrorRed else SuccessGreen
                    ),
                    enabled = uiState.isTextLoaded && !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (uiState.isReading) "Остановить чтение" else "Начать чтение")
                }

                Spacer(modifier = Modifier.width(18.dp))

                Button(
                    onClick = onShowPasswordDialog,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading
                ) {
                    Text("Ввести пароль")
                }
            }
        }

        if (uiState.showPasswordDialog) {
            AlertDialog(
                onDismissRequest = onCancelPasswordDialog,
                title = { Text(text = "Введите пароль") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Пароль") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (!uiState.passwordError.isNullOrEmpty()) {
                            Text(
                                text = uiState.passwordError,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onPasswordEntered(passwordInput)
                        passwordInput = ""
                    }) {
                        Text("Подтвердить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancelPasswordDialog) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}
