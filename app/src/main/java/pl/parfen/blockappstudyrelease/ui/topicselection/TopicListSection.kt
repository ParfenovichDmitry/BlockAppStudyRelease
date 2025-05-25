package pl.parfen.blockappstudyrelease.ui.topicselection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.theme.BlueLight
import pl.parfen.blockappstudyrelease.ui.theme.GreenLight

@Composable
fun TopicListSection(
    userTopics: List<String>,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultTopics = stringArrayResource(R.array.default_topics)
    var topicToDelete by remember { mutableStateOf<String?>(null) }

    if (topicToDelete != null) {
        Dialog(onDismissRequest = { topicToDelete = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlueLight, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.confirm_deletion_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.confirm_deletion_message, topicToDelete ?: ""),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.cancel),
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures { topicToDelete = null }
                                },
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.delete),
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        onRemove(topicToDelete!!)
                                        topicToDelete = null
                                    }
                                },
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
            .border(2.dp, Color.DarkGray, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.topics_button_label),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(8.dp)
            )
        }

        items(userTopics) { topic ->
            val isUserTopic = topic !in defaultTopics

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 8.dp)
                    .background(
                        if (isUserTopic) GreenLight else Color.Transparent,
                        RoundedCornerShape(6.dp)
                    )
                    .pointerInput(Unit) {
                        if (isUserTopic) {
                            detectTapGestures(onLongPress = { topicToDelete = topic })
                        }
                    }
            ) {
                Text(
                    text = topic,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}