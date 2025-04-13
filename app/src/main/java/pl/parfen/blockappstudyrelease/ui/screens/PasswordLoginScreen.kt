package pl.parfen.blockappstudyrelease.ui.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.password.components.CustomImageButton
import pl.parfen.blockappstudyrelease.ui.password.components.PasswordTextField
import pl.parfen.blockappstudyrelease.ui.theme.*

@Composable
fun PasswordLoginScreen(
    onLogin: (String) -> Unit,
    onForgotPassword: () -> Unit,
    onCancel: () -> Unit,
    checkPassword: (String) -> Boolean
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(GreenLight, GreenMedium)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.enter_password),
                style = MaterialTheme.typography.headlineMedium,
                color = ButtonTextColor,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
            )

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(id = R.string.password_hint),
                isVisible = passwordVisible,
                onVisibilityToggle = { passwordVisible = !passwordVisible },
                textColor = ButtonTextColor,
                labelColor = LabelFocusedColor
            )

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = ErrorRed,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomImageButton(
                    normalResId = R.drawable.yes_green,
                    pressedResId = R.drawable.yes_press,
                    contentDescription = stringResource(R.string.login),
                    text = stringResource(R.string.login),
                    textColor = White,
                    onClick = {
                        if (password.isNotEmpty()) {
                            if (checkPassword(password)) {
                                onLogin(password)
                            } else {
                                errorMessage = context.getString(R.string.error_password_incorrect)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.error_password_incorrect),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        } else {
                            errorMessage = context.getString(R.string.error_empty_fields)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.error_empty_fields),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp)
                )

                CustomImageButton(
                    normalResId = R.drawable.no_red,
                    pressedResId = R.drawable.no_pres,
                    contentDescription = stringResource(R.string.cancel),
                    text = stringResource(R.string.cancel),
                    textColor = White,
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp)
                )
            }
        }

        // КНОПКА "ЗАБЫЛ ПАРОЛЬ" ВНИЗУ ПРАВОГО УГЛА
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            CustomImageButton(
                normalResId = R.drawable.no_red,
                pressedResId = R.drawable.no_pres,
                contentDescription = stringResource(R.string.forgot_password),
                text = stringResource(R.string.forgot_password),
                textColor = White,
                onClick = onForgotPassword,
                modifier = Modifier
                    .width(220.dp)
                    .height(70.dp)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}
