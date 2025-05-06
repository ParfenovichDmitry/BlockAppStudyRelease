package pl.parfen.blockappstudyrelease.ui.books

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.model.StorageType
import pl.parfen.blockappstudyrelease.ui.books.components.BookHeader
import pl.parfen.blockappstudyrelease.ui.books.components.BookItem
import pl.parfen.blockappstudyrelease.ui.components.ImageButton
import pl.parfen.blockappstudyrelease.ui.theme.GreenLight
import pl.parfen.blockappstudyrelease.ui.theme.GreenMedium
import pl.parfen.blockappstudyrelease.viewmodel.BookViewModel

@Composable
fun BookScreen(
    viewModel: BookViewModel = viewModel(),
    profileId: Int,
    age: String,
    primaryLanguage: String,
    onSave: (Boolean, String?) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val languageNames = context.resources.getStringArray(R.array.available_languages).toList()
    val languageCodes = context.resources.getStringArray(R.array.language_codes).toList()
    val withoutLangText = stringResource(R.string.without_additional_language)
    val availableDropdownItems = listOf(withoutLangText) + languageNames.filter { it != primaryLanguage }

    var dropdownExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    var scrollPerformed by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var bookToDelete by remember { mutableStateOf<Book?>(null) }

    val filePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { viewModel.addUserBook(it, profileId) }
        }

    var currentAge by remember { mutableStateOf(age) }

    LaunchedEffect(profileId, primaryLanguage) {
        viewModel.restoreUiStateFromProfile(profileId, age, primaryLanguage)
        currentAge = age
    }

    LaunchedEffect(uiState.books, uiState.selectedBookTitle) {
        if (!scrollPerformed && uiState.books.isNotEmpty() && uiState.selectedBookTitle.isNotEmpty()) {
            val index = uiState.books.indexOfFirst { it.title == uiState.selectedBookTitle }
            if (index != -1) {
                listState.scrollToItem(index)
                scrollPerformed = true
            }
        }
    }

    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
        if (uiState.isProfileLoaded) {
            viewModel.saveScrollPosition(listState.firstVisibleItemIndex)
        }
    }

    LaunchedEffect(uiState.showAllBooks, uiState.secondaryLanguage, currentAge) {
        if (uiState.isProfileLoaded) {
            viewModel.loadBooks(
                age = currentAge,
                primaryLanguage = uiState.primaryLanguage,
                secondaryLanguage = uiState.secondaryLanguage,
                showAllBooks = uiState.showAllBooks,
                includeUserBooks = true,
                profileId = profileId
            )
        }
    }

    LaunchedEffect(uiState.selectedAdditionalLanguageIndex) {
        dropdownExpanded = false
    }

    if (showDeleteDialog && bookToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                bookToDelete = null
            },
            title = { Text(stringResource(R.string.confirm_deletion_title)) },
            text = { Text(stringResource(R.string.confirm_deletion_message, bookToDelete?.title.orEmpty())) },
            confirmButton = {
                Button(onClick = {
                    bookToDelete?.let { viewModel.deleteUserBook(it, profileId) }
                    showDeleteDialog = false
                    bookToDelete = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDeleteDialog = false
                    bookToDelete = null
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (!uiState.isProfileLoaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(GreenLight, GreenMedium))),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(GreenLight, GreenMedium)))
                .padding(16.dp)
        ) {
            BookHeader(language = uiState.primaryLanguage, age = currentAge.ifEmpty { "?" })

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.showAllBooks,
                    onCheckedChange = { viewModel.updateShowAllBooks(it) }
                )
                Text(text = stringResource(R.string.show_all_books), modifier = Modifier.weight(1f))

                Box {
                    Button(onClick = { dropdownExpanded = true }) {
                        val langDisplay = uiState.secondaryLanguage?.let {
                            val i = languageCodes.indexOf(it)
                            languageNames.getOrNull(i)
                        } ?: withoutLangText
                        Text(langDisplay)
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        availableDropdownItems.forEachIndexed { index, lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    val langCode = if (index == 0) null else languageCodes[languageNames.indexOf(lang)]
                                    viewModel.updateSelectedAdditionalLanguage(langCode)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ImageButton(
                text = stringResource(R.string.add_book),
                normalImageRes = R.drawable.yes_green,
                pressedImageRes = R.drawable.yes_press,
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    !uiState.error.isNullOrBlank() -> Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    else -> LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.books) { book ->
                            BookItem(
                                book = book,
                                isSelected = book.title == uiState.selectedBookTitle,
                                onClick = { viewModel.selectBook(book.title) },
                                onPreview = { viewModel.openPreview(context, book) },
                                onLongClick = {
                                    if (book.isUserBook && book.storageType != StorageType.ASSETS) {
                                        bookToDelete = book
                                        showDeleteDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ImageButton(
                    text = stringResource(R.string.save),
                    normalImageRes = R.drawable.yes_green,
                    pressedImageRes = R.drawable.yes_press,
                    onClick = {
                        viewModel.saveChanges(
                            profileId = profileId,
                            showAllBooks = uiState.showAllBooks,
                            secondaryLanguage = uiState.secondaryLanguage,
                            onSaveComplete = onSave
                        )
                    },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                ImageButton(
                    text = stringResource(R.string.cancel),
                    normalImageRes = R.drawable.no_red,
                    pressedImageRes = R.drawable.no_pres,
                    onClick = {
                        viewModel.cancelChanges()
                        onCancel()
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }
        }
    }
}
