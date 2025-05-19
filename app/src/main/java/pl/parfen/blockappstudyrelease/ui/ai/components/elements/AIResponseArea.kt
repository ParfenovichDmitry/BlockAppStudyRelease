package pl.parfen.blockappstudyrelease.ui.ai.components.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.theme.*

@Composable
fun AIResponseArea(
    aiResponse: String,
    isLoading: Boolean,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> item {
                    Text(
                        text = stringResource(id = R.string.loading_placeholder),
                        fontSize = 16.sp,
                        color = TitleTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BlueLight, RoundedCornerShape(8.dp))
                            .border(1.dp, BlueMedium, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                }

                aiResponse.isNotBlank() -> {
                    val lines = aiResponse.trim().split("\n").filter { it.isNotBlank() }
                    items(lines) { line ->
                        Text(
                            text = line,
                            fontSize = 16.sp,
                            color = TitleTextColor,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BlueLight, RoundedCornerShape(8.dp))
                                .border(1.dp, BlueMedium, RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        )
                    }
                }

                else -> item {
                    Text(
                        text = stringResource(id = R.string.no_result),
                        fontSize = 16.sp,
                        color = TitleTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LanguageLabelBackground, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
