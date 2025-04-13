package pl.parfen.blockappstudyrelease.ui.selectapp.components

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.ui.theme.BlueLight
import pl.parfen.blockappstudyrelease.ui.theme.White

@Composable
fun AppItem(
    appInfo: ApplicationInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    val appName by produceState(initialValue = "", key1 = appInfo.packageName) {
        value = withContext(Dispatchers.IO) {
            packageManager.getApplicationLabel(appInfo).toString()
        }
    }

    val appIcon by produceState(
        initialValue = null as android.graphics.drawable.Drawable?,
        key1 = appInfo.packageName
    ) {
        value = withContext(Dispatchers.IO) {
            packageManager.getApplicationIcon(appInfo)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = if (isSelected) BlueLight else White,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (appIcon != null) {
            Image(
                painter = rememberAsyncImagePainter(model = appIcon),
                contentDescription = appName,
                modifier = Modifier.size(40.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = appName,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        Checkbox(
            checked = isSelected,
            onCheckedChange = { onClick() }
        )
    }
}