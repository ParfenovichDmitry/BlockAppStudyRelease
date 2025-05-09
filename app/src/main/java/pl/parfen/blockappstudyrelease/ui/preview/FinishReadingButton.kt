package pl.parfen.blockappstudyrelease.ui.preview

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.viewmodel.BookViewModel

@Composable
fun FinishReadingButton(
    bookTitle: String,
    listState: LazyListState,
    bookViewModel: BookViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    val image: Painter = if (isPressed) {
        painterResource(id = R.drawable.yes_press)
    } else {
        painterResource(id = R.drawable.yes_green)
    }

    Box(
        modifier = Modifier
            .size(width = 134.dp, height = 67.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                isPressed = true
                scope.launch {
                    val index = listState.firstVisibleItemIndex
                    Log.d("BookSave", "First visible index on save: $index")
                    bookViewModel.saveScrollPosition(bookTitle, index)
                    activity?.finish()
                }
                isPressed = false
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = image,
            contentDescription = "OK",
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "OK",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}