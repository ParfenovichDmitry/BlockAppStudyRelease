package pl.parfen.blockappstudyrelease.ui.ai.components.elements

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.TopicSelectionActivity
import pl.parfen.blockappstudyrelease.ui.ai.components.Topic
import pl.parfen.blockappstudyrelease.ui.theme.LanguageLabelText

@Composable
fun AITopicSelector(
    userTopics: List<Topic>,
    selectedTopicsState: List<String>,
    onTopicsUpdated: (List<Topic>, List<String>) -> Unit,
    context: Context,
    selectedTopicIndex: Int,
    onTopicSelected: (Int) -> Unit,
    generateOnlySelectedTopics: Boolean,
    onGenerateOnlySelectedChange: (Boolean) -> Unit
) {
    val localContext = LocalContext.current
    val expanded = remember { mutableStateOf(false) }
    val selectedTopics = remember { mutableStateListOf<String>().apply { addAll(selectedTopicsState) } }

    Column(modifier = Modifier.fillMaxWidth()) {
        val interaction = remember { MutableInteractionSource() }
        val isPressed by interaction.collectIsPressedAsState()
        val painter: Painter = if (isPressed) {
            painterResource(id = R.drawable.yes_press)
        } else {
            painterResource(id = R.drawable.yes_green)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable(
                    interactionSource = interaction,
                    indication = null
                ) {
                    localContext.startActivity(Intent(localContext, TopicSelectionActivity::class.java))
                }
        ) {
            Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.topics_button_label),
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            OutlinedButton(
                onClick = { expanded.value = !expanded.value },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.selected_topics_display, selectedTopics.joinToString()))
            }

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LanguageLabelText)
            ) {
                userTopics.forEach { topic ->
                    val isSelected = selectedTopics.contains(topic.original)

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = topic.original,
                                    color = androidx.compose.ui.graphics.Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            if (isSelected) {
                                selectedTopics.remove(topic.original)
                            } else {
                                selectedTopics.add(topic.original)
                            }
                            onTopicsUpdated(userTopics, selectedTopics.toList())
                        }
                    )
                }
            }
        }
    }
}
