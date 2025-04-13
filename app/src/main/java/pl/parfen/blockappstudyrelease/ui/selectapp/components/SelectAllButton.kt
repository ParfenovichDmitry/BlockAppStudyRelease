package pl.parfen.blockappstudyrelease.ui.selectapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R

@Composable
fun SelectAllButton(
    isAllSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(
                if (isAllSelected) R.drawable.yes_press else R.drawable.yes_green
            ),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.FillBounds
        )


        Text(
            text = if (isAllSelected) stringResource(R.string.deselect_all) else stringResource(R.string.select_all),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )
    }
}