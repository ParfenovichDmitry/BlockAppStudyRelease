package pl.parfen.blockappstudyrelease.ui.selectapp

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.ui.selectapp.components.AppItem
import pl.parfen.blockappstudyrelease.ui.selectapp.components.SaveCancelButtons
import pl.parfen.blockappstudyrelease.ui.selectapp.components.SelectAllButton
import pl.parfen.blockappstudyrelease.ui.selectapp.components.TopTitle
import pl.parfen.blockappstudyrelease.ui.selectapps.SelectAppViewModel
import pl.parfen.blockappstudyrelease.ui.theme.GreenLight
import pl.parfen.blockappstudyrelease.ui.theme.GreenMedium

@Composable
fun SelectAppScreen(
    viewModel: SelectAppViewModel,
    onSave: (List<String>) -> Unit,
    onCancel: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isAppsLoaded by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (!isAppsLoaded) {
            val apps = withContext(Dispatchers.IO) {
                loadInstalledApps(context)
            }
            viewModel.setAppList(apps)
            isAppsLoaded = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = listOf(GreenLight, GreenMedium))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopTitle(text = stringResource(R.string.selected_apps_title))
            Spacer(modifier = Modifier.height(8.dp))

            if (!isAppsLoaded) {
                CircularProgressIndicator()
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 12.dp),
                        state = scrollState
                    ) {
                        items(uiState.appList) { appInfo ->
                            AppItem(
                                appInfo = appInfo,
                                isSelected = uiState.selectedApps.contains(appInfo.packageName),
                                onClick = { viewModel.toggleAppSelection(appInfo.packageName) }
                            )
                        }
                    }

                    // Скроллбар
                    val totalItems = uiState.appList.size
                    val visibleItems = scrollState.layoutInfo.visibleItemsInfo.size
                    val firstVisibleItemIndex = scrollState.firstVisibleItemIndex

                    val scrollFraction = if (totalItems > visibleItems && visibleItems > 0) {
                        firstVisibleItemIndex.toFloat() / (totalItems - visibleItems)
                    } else 0f

                    val thumbHeightFraction = if (totalItems > 0) {
                        (visibleItems.toFloat() / totalItems).coerceIn(0.1f, 1f)
                    } else 0f

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(18.dp)
                            .fillMaxHeight()
                            .padding(end = 4.dp)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val totalScrollableItems =
                                        (totalItems - visibleItems).coerceAtLeast(1)
                                    val scrollDelta =
                                        (dragAmount.y / size.height) * totalScrollableItems
                                    val newIndex =
                                        (scrollState.firstVisibleItemIndex + scrollDelta.toInt())
                                            .coerceIn(0, totalScrollableItems)
                                    coroutineScope.launch {
                                        scrollState.scrollToItem(newIndex)
                                    }
                                }
                            }
                    ) {
                        val visibleListHeight =
                            scrollState.layoutInfo.viewportEndOffset -
                                    scrollState.layoutInfo.viewportStartOffset

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    translationY = scrollFraction *
                                            (visibleListHeight - visibleListHeight * thumbHeightFraction)
                                }
                                .fillMaxHeight(thumbHeightFraction)
                                .background(
                                    color = Color.Gray.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SelectAllButton(
                    isAllSelected = uiState.isAllSelected,
                    onToggle = { viewModel.toggleSelectAll() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                SaveCancelButtons(
                    onSave = { onSave(uiState.selectedApps) },
                    onCancel = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

private suspend fun loadInstalledApps(context: Context): List<ApplicationInfo> {
    val packageManager = context.packageManager
    return withContext(Dispatchers.IO) {
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter {
                packageManager.getLaunchIntentForPackage(it.packageName) != null &&
                        it.packageName != context.packageName
            }
    }
}
