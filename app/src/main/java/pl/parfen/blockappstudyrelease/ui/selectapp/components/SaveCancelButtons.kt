package pl.parfen.blockappstudyrelease.ui.selectapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.profiles.components.ImageButton

@Composable
fun SaveCancelButtons(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        ImageButton(
            text = stringResource(R.string.save),
            normalImageRes = R.drawable.yes_green,
            pressedImageRes = R.drawable.yes_press,
            isPressed = false,
            onClick = onSave,
            textColor = Color.White,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .padding(end = 8.dp)
        )


        ImageButton(
            text = stringResource(R.string.cancel),
            normalImageRes = R.drawable.no_red,
            pressedImageRes = R.drawable.no_pres,
            isPressed = false,
            onClick = onCancel,
            textColor = Color.White,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .padding(start = 8.dp)
        )
    }
}