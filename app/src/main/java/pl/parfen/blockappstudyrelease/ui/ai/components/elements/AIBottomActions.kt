package pl.parfen.blockappstudyrelease.ui.ai.components.elements

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.ai.components.Topic

@Composable
fun AIBottomActions(
    context: Context,
    profileId: Int,
    aiNetwork: String,
    userTopics: List<Topic>,
    aiLanguage: String,
    selectedTopics: List<String>,
    additionalLanguage: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val saveInteractionSource = remember { MutableInteractionSource() }
        val isSavePressed by saveInteractionSource.collectIsPressedAsState()
        val saveBackground: Painter = if (isSavePressed) {
            painterResource(id = R.drawable.yes_press)
        } else {
            painterResource(id = R.drawable.yes_green)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .height(48.dp)
                .clickable(
                    interactionSource = saveInteractionSource,
                    indication = null
                ) {
                    val activity = context as? Activity ?: return@clickable
                    val intent = Intent().apply {
                        putExtra("aiNetwork", aiNetwork)
                        putStringArrayListExtra("aiTopics", ArrayList(userTopics.map { it.original }))
                        putExtra("aiLanguage", aiLanguage)
                        putExtra("additionalLanguage", additionalLanguage)
                        putStringArrayListExtra("selectedTopics", ArrayList(selectedTopics))
                    }
                    activity.setResult(Activity.RESULT_OK, intent)
                    activity.finish()
                }
        ) {
            Image(painter = saveBackground, contentDescription = null, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.save),
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
        }

        val cancelInteractionSource = remember { MutableInteractionSource() }
        val isCancelPressed by cancelInteractionSource.collectIsPressedAsState()
        val cancelBackground: Painter = if (isCancelPressed) {
            painterResource(id = R.drawable.no_pres)
        } else {
            painterResource(id = R.drawable.no_red)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .height(48.dp)
                .clickable(
                    interactionSource = cancelInteractionSource,
                    indication = null
                ) {
                    (context as? Activity)?.finish()
                }
        ) {
            Image(painter = cancelBackground, contentDescription = null, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}
