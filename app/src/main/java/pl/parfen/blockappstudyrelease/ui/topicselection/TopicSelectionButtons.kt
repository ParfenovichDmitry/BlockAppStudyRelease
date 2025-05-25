package pl.parfen.blockappstudyrelease.ui.topicselection

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.components.ImageButton

@Composable
fun TopicSelectionButtons(
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ImageButton(
            text = stringResource(id = R.string.save),
            normalImageRes = R.drawable.yes_green,
            pressedImageRes = R.drawable.yes_press,
            onClick = onSave,
            textColor = colorResource(id = R.color.white),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        ImageButton(
            text = stringResource(id = R.string.cancel),
            normalImageRes = R.drawable.no_red,
            pressedImageRes = R.drawable.no_pres,
            onClick = onCancel,
            textColor = colorResource(id = R.color.white),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }
}
