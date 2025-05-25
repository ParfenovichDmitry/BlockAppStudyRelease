package pl.parfen.blockappstudyrelease.ui.topicselection

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.components.ImageButton
import pl.parfen.blockappstudyrelease.ui.theme.GreenLight
import pl.parfen.blockappstudyrelease.ui.theme.GreenMedium
import pl.parfen.blockappstudyrelease.ui.theme.White

@Composable
fun TopicSelectionScreen(
    currentTopics: List<String>,
    onSave: (List<String>) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val defaultTopics = remember {
        context.resources.getStringArray(R.array.default_topics).toList()
    }

    val prefs = remember {
        context.getSharedPreferences("user_topics", Context.MODE_PRIVATE)
    }

    val savedCustomTopics = prefs.getStringSet("user_topics", emptySet())?.toMutableSet() ?: mutableSetOf()
    var userTopics by remember { mutableStateOf(savedCustomTopics.toList()) }
    var newTopic by remember { mutableStateOf("") }

    val combinedTopics: List<String> = remember(userTopics) {
        (defaultTopics + userTopics).distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(GreenLight, GreenMedium)))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.topic_selection_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TopicInputField(
                newTopic = newTopic,
                onTopicChange = { newTopic = it },
                onAddClick = {
                    val trimmed = newTopic.trim()
                    if (trimmed.isNotBlank() && trimmed !in combinedTopics) {
                        userTopics = userTopics + trimmed
                        newTopic = ""

                        val updatedSet = savedCustomTopics + trimmed
                        prefs.edit().putStringSet("user_topics", updatedSet.toSet()).apply()
                    }
                },
                addButtonRes = R.drawable.yes_green,
                addButtonPressedRes = R.drawable.yes_press
            )

            Spacer(modifier = Modifier.height(12.dp))

            TopicListSection(
                userTopics = combinedTopics,
                onRemove = { toRemove ->
                    if (toRemove in userTopics) {
                        userTopics = userTopics - toRemove
                        val updatedSet = savedCustomTopics - toRemove
                        prefs.edit().putStringSet("user_topics", updatedSet.toSet()).apply()
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ImageButton(
                text = stringResource(R.string.save),
                normalImageRes = R.drawable.yes_green,
                pressedImageRes = R.drawable.yes_press,
                onClick = { onSave(combinedTopics) },
                textColor = White,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )

            ImageButton(
                text = stringResource(R.string.cancel),
                normalImageRes = R.drawable.no_red,
                pressedImageRes = R.drawable.no_pres,
                onClick = onCancel,
                textColor = White,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }
    }
}
