package pl.parfen.blockappstudyrelease.ui.createprofil.components

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import pl.parfen.blockappstudyrelease.R

@Composable
fun AvatarPicker(
    avatarUri: Uri?,
    onAvatarSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onAvatarSelected(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            tempCameraUri?.let { onAvatarSelected(it) }
        }
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color(0xFF9BCB9D))
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        if (avatarUri != null) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = avatarUri,
                    placeholder = painterResource(R.drawable.ava_tmp), // Заменить на ваш ресурс
                    error = painterResource(R.drawable.ava_tmp)
                ),
                contentDescription = stringResource(R.string.avatar),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = stringResource(R.string.avatar),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.choose_avatar)) },
            text = { Text(stringResource(R.string.choose_avatar_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        val uri = context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            ContentValues()
                        )
                        if (uri != null) {
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            Toast.makeText(context, "Не удалось открыть камеру", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.camera_option),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text(
                        text = stringResource(R.string.gallery_option),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        )
    }
}
