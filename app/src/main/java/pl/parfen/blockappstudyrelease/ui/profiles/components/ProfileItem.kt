package pl.parfen.blockappstudyrelease.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.model.Profile

@Composable
fun ProfileItem(
    profile: Profile,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDeleteConfirmed: (Profile) -> Unit // ✅ Теперь принимаем профиль на удаление
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(150.dp, 180.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFF00FF00) else Color.White)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { showDialog = true } // ✅ Долгое нажатие вызывает удаление
                )
            }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val avatarHeight = 150.dp * 0.7f

            if (!profile.avatar.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = profile.avatar,
                        placeholder = painterResource(R.drawable.ava_tmp),
                        error = painterResource(R.drawable.ava_tmp)
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .height(avatarHeight)
                        .width(120.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ava_tmp),
                    contentDescription = stringResource(id = R.string.avatar),
                    modifier = Modifier
                        .height(avatarHeight)
                        .width(120.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = profile.nickname.ifEmpty { stringResource(id = R.string.no_name) },
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(R.string.profile_age, profile.age),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = stringResource(R.string.delete_profile_title))
            },
            text = {
                Text(text = stringResource(R.string.delete_profile_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteConfirmed(profile) // ✅ Передаём профиль на удаление
                        showDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}
