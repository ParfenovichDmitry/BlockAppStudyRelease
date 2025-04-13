package pl.parfen.blockappstudyrelease.ui.password

import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.repository.PasswordRepository
import pl.parfen.blockappstudyrelease.ui.password.components.CustomImageButton
import pl.parfen.blockappstudyrelease.ui.password.components.PasswordTextField
import pl.parfen.blockappstudyrelease.ui.theme.*

@Composable
fun PasswordRecoveryScreen(
    onLogin: () -> Unit,
    onResetPassword: () -> Unit,
    viewModel: PasswordRecoveryViewModel = viewModel()
) {
    val context = LocalContext.current

    val savedQuestion = PasswordRepository.getSecretQuestion(context) ?: ""
    val savedAnswer = PasswordRepository.getSecretAnswer(context)
    val encryptedPassword = PasswordRepository.getEncryptedPassword(context)

    var userAnswer by remember { mutableStateOf("") }
    val decryptedPassword by viewModel.decryptedPassword.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(GreenLight, GreenMedium)))
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = savedQuestion,
                style = MaterialTheme.typography.headlineSmall,
                color = ButtonTextColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            PasswordTextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                label = stringResource(R.string.answer_hint),
                isVisible = true,
                onVisibilityToggle = {},
                textColor = ButtonTextColor,
                labelColor = LabelFocusedColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomImageButton(
                normalResId = R.drawable.yes_green,
                pressedResId = R.drawable.yes_press,
                contentDescription = stringResource(R.string.confirm),
                text = stringResource(R.string.confirm),
                textColor = White,
                onClick = {
                    if (!encryptedPassword.isNullOrBlank() && !savedAnswer.isNullOrBlank()) {
                        viewModel.decryptPassword(
                            encryptedPassword,
                            userAnswer,
                            savedAnswer
                        ) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_wrong_answer),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_no_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.your_password),
                color = ErrorRed,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = decryptedPassword ?: "",
                color = ButtonTextColor,
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomImageButton(
                    normalResId = R.drawable.yes_green,
                    pressedResId = R.drawable.yes_press,
                    contentDescription = stringResource(R.string.login),
                    text = stringResource(R.string.login),
                    textColor = White,
                    onClick = onLogin,
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp)
                )

                CustomImageButton(
                    normalResId = R.drawable.no_red,
                    pressedResId = R.drawable.no_pres,
                    contentDescription = stringResource(R.string.reset_password),
                    text = stringResource(R.string.reset_password),
                    textColor = White,
                    onClick = onResetPassword,
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp)
                )
            }
        }
    }
}
