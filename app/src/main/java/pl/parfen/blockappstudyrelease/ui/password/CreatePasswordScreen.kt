package pl.parfen.blockappstudyrelease.ui.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.password.components.CustomImageButton
import pl.parfen.blockappstudyrelease.ui.password.components.PasswordTextField
import pl.parfen.blockappstudyrelease.ui.theme.*

@Composable
fun CreatePasswordScreen(
    onSavePassword: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var password by rememberSaveable { mutableStateOf("") }
    var reEnteredPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var reEnteredPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(GreenLight, GreenMedium)))
            .padding(24.dp)
    ) {
        // Заголовок
        Text(
            text = stringResource(R.string.enter_password),
            style = MaterialTheme.typography.headlineMedium,
            color = ButtonTextColor,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        // Поле ввода пароля
        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password_hint),
            isVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible },
            textColor = ButtonTextColor,
            labelColor = LabelFocusedColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Подпись ко второму полю
        Text(
            text = stringResource(R.string.reenter_password),
            style = MaterialTheme.typography.titleMedium,
            color = ButtonTextColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Поле повторного ввода
        PasswordTextField(
            value = reEnteredPassword,
            onValueChange = { reEnteredPassword = it },
            label = stringResource(R.string.password_hint),
            isVisible = reEnteredPasswordVisible,
            onVisibilityToggle = { reEnteredPasswordVisible = !reEnteredPasswordVisible },
            textColor = ButtonTextColor,
            labelColor = LabelFocusedColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ошибка
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
                    .padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопки
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Кнопка Сохранить
            CustomImageButton(
                normalResId = R.drawable.yes_green,
                pressedResId = R.drawable.yes_press,
                contentDescription = stringResource(R.string.save),
                text = stringResource(R.string.save),
                textColor = White,
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp),
                onClick = {
                    errorMessage = when {
                        password.isEmpty() || reEnteredPassword.isEmpty() ->
                            context.getString(R.string.error_empty_fields)
                        password != reEnteredPassword ->
                            context.getString(R.string.error_password_mismatch)
                        else -> {
                            onSavePassword(password)
                            null
                        }
                    }
                }
            )

            // Кнопка Отмена
            CustomImageButton(
                normalResId = R.drawable.no_red,
                pressedResId = R.drawable.no_pres,
                contentDescription = stringResource(R.string.cancel),
                text = stringResource(R.string.cancel),
                textColor = White,
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp),
                onClick = {
                    password = ""
                    reEnteredPassword = ""
                    errorMessage = null
                    onCancel()
                }
            )
        }
    }
}
