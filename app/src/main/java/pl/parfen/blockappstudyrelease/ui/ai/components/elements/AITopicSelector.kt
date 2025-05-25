package pl.parfen.blockappstudyrelease.ui.ai.components.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.ai.components.Topic
import pl.parfen.blockappstudyrelease.ui.theme.SuccessGreen
import pl.parfen.blockappstudyrelease.ui.theme.White

@Composable
fun AITopicSelector(
    userTopics: List<Topic>,
    selectedTopicsState: List<String>,
    onTopicsUpdated: (List<Topic>, List<String>) -> Unit,
    context: android.content.Context,
    selectedTopicIndex: Int,
    onTopicSelected: (Int) -> Unit,
    generateOnlySelectedTopics: Boolean,
    onGenerateOnlySelectedChange: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // "Кнопка" для раскрытия списка тем
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(SuccessGreen, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
            .padding(horizontal = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (selectedTopicsState.isNotEmpty()) {
                selectedTopicsState.joinToString(", ")
            } else {
                stringResource(R.string.all_topics)
            },
            color = White,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(
            text = if (expanded) "▲" else "▼",
            color = White,
            modifier = Modifier.padding(end = 8.dp)
        )
    }

    // Список раскрывается/сворачивается
    if (expanded) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SuccessGreen, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            userTopics.forEachIndexed { index, topic ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .toggleable(
                            value = selectedTopicsState.contains(topic.original),
                            onValueChange = { checked ->
                                val newSelected = if (checked) {
                                    selectedTopicsState + topic.original
                                } else {
                                    selectedTopicsState - topic.original
                                }
                                onTopicsUpdated(userTopics, newSelected)
                            }
                        )
                ) {
                    Checkbox(
                        checked = selectedTopicsState.contains(topic.original),
                        onCheckedChange = null
                    )
                    Text(
                        text = topic.original,
                        color = White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
