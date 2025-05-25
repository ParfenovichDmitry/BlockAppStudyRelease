package pl.parfen.blockappstudyrelease.ui.topicselection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.components.ImageButton

@Composable
fun TopicInputField(
    newTopic: String,
    onTopicChange: (String) -> Unit,
    onAddClick: () -> Unit,
    addButtonRes: Int,
    addButtonPressedRes: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = newTopic,
            onValueChange = onTopicChange,
            label = { Text(stringResource(R.string.topic_input_label)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ImageButton(
            text = stringResource(id = R.string.topic_add_button),
            normalImageRes = addButtonRes,
            pressedImageRes = addButtonPressedRes,
            onClick = onAddClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}
