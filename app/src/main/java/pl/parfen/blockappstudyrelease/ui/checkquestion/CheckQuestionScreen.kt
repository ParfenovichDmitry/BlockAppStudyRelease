package pl.parfen.blockappstudyrelease.ui.checkquestion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.checkquestion.components.SecretQuestionDropdown
import pl.parfen.blockappstudyrelease.ui.password.components.CustomImageButton
import pl.parfen.blockappstudyrelease.ui.theme.ButtonTextColor
import pl.parfen.blockappstudyrelease.ui.theme.ErrorRed
import pl.parfen.blockappstudyrelease.ui.theme.GreenLight
import pl.parfen.blockappstudyrelease.ui.theme.GreenMedium
import pl.parfen.blockappstudyrelease.ui.theme.LabelFocusedColor
import pl.parfen.blockappstudyrelease.ui.theme.White
import pl.parfen.blockappstudyrelease.viewmodel.CheckQuestionViewModel

@Composable
fun CheckQuestionScreen(
    viewModel: CheckQuestionViewModel,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    val questions = stringArrayResource(id = R.array.secret_questions)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(GreenLight, GreenMedium)))
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_secret_question),
            style = MaterialTheme.typography.headlineMedium,
            color = ButtonTextColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        SecretQuestionDropdown(
            questions = questions.toList(),
            selectedQuestion = state.selectedQuestion,
            onQuestionSelected = { viewModel.selectQuestion(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = state.answer,
            onValueChange = { viewModel.updateAnswer(it) },
            label = { Text(stringResource(id = R.string.answer_hint)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedTextColor = ButtonTextColor,
                unfocusedTextColor = ButtonTextColor,
                focusedLabelColor = LabelFocusedColor,
                unfocusedLabelColor = LabelFocusedColor
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = state.errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = state.errorMessage.orEmpty(),
                color = ErrorRed,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomImageButton(
                normalResId = R.drawable.yes_green,
                pressedResId = R.drawable.yes_press,
                contentDescription = stringResource(R.string.save),
                text = stringResource(R.string.save),
                textColor = White,
                onClick = {
                    viewModel.validateAndSave(context, onSaveSuccess)
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
                onClick = {
                    onCancel()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
            )
        }
    }
}
