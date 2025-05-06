package pl.parfen.blockappstudyrelease.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.viewmodel.BookPreviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPreviewScreen(
    viewModel: BookPreviewViewModel,
    book: Book,
    listState: LazyListState
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // При старте загрузить книгу
    LaunchedEffect(book) {
        viewModel.loadBook(book)
    }

    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
        viewModel.updateScroll(
            profileId = uiState.profileId,
            firstVisibleLine = listState.firstVisibleItemIndex
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.book?.title.orEmpty(),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    Text(
                        text = "${uiState.currentPage}/${uiState.totalPages} (${uiState.progressPercent}%)",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(uiState.lines) { _, line ->
                        Text(
                            text = line,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.onCompleteReading()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Завершить", fontSize = 18.sp)
                }
            }
        }
    }
}
