package pl.parfen.blockappstudyrelease.ui.preview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.ui.theme.GreenDarkEnd
import pl.parfen.blockappstudyrelease.ui.theme.GreenDarkStart
import pl.parfen.blockappstudyrelease.viewmodel.BookPreviewViewModel
import pl.parfen.blockappstudyrelease.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPreviewScreen(
    viewModel: BookPreviewViewModel,
    book: Book,
    listState: LazyListState,
    profileId: Int,
    initialScrollPosition: Int,
    bookViewModel: BookViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var hasScrolled by remember { mutableStateOf(false) }

    LaunchedEffect(book) {
        viewModel.init(profileId, initialScrollPosition)
        viewModel.loadBook(context, book)
    }

    LaunchedEffect(uiState.isLoading, uiState.lines) {
        if (!uiState.isLoading && uiState.lines.isNotEmpty() && !hasScrolled) {
            scope.launch {
                listState.scrollToItem(uiState.scrollPosition, uiState.scrollOffset)
                hasScrolled = true
                viewModel.onScrollRestored()
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.distinctUntilChanged().collectLatest { (index, offset) ->
            viewModel.updateScroll(index, offset)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.book?.title.orEmpty(),
                        color = Color.White
                    )
                },
                actions = {
                    Text(
                        text = "${uiState.currentPage}/${uiState.totalPages} (${uiState.progressPercent}%)",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Brush.verticalGradient(
                        listOf(GreenDarkStart, GreenDarkEnd)
                    ).toColor()
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Brush.verticalGradient(listOf(GreenDarkStart, GreenDarkEnd)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 72.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, shape = MaterialTheme.shapes.medium)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 28.dp, start = 16.dp, top = 16.dp, bottom = 16.dp)
                    ) {
                        itemsIndexed(uiState.lines) { _, line ->
                            Text(
                                text = line,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(24.dp)
                            .padding(vertical = 16.dp)
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    val scrollAreaHeight = size.height.toFloat()
                                    val dragY = change.position.y.coerceIn(0f, scrollAreaHeight)
                                    val proportion = dragY / scrollAreaHeight
                                    val totalItems = listState.layoutInfo.totalItemsCount
                                    val targetIndex = (totalItems * proportion).toInt().coerceIn(0, totalItems - 1)

                                    scope.launch {
                                        listState.scrollToItem(targetIndex)
                                    }
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val totalItems = listState.layoutInfo.totalItemsCount
                            val visibleItems = listState.layoutInfo.visibleItemsInfo.size
                            if (totalItems > 0 && visibleItems > 0) {
                                val proportion = visibleItems.toFloat() / totalItems
                                val scrollbarHeight = size.height * proportion
                                val scrollOffset = listState.firstVisibleItemIndex.toFloat() / totalItems
                                val scrollbarOffsetY = size.height * scrollOffset

                                val barWidth = size.width * 0.6f
                                val offsetX = (size.width - barWidth) / 2

                                drawRoundRect(
                                    color = Color.Gray,
                                    topLeft = Offset(offsetX, scrollbarOffsetY),
                                    size = androidx.compose.ui.geometry.Size(barWidth, scrollbarHeight),
                                    cornerRadius = CornerRadius(6.dp.toPx())
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp)
            ) {
                FinishReadingButton(
                    bookTitle = book.title,
                    listState = listState,
                    bookViewModel = bookViewModel
                )
            }
        }
    }
}

private fun Brush.toColor(): Color = GreenDarkEnd.copy(alpha = 0.95f)