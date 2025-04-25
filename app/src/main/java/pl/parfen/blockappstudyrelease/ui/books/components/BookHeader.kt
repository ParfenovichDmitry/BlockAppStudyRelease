package pl.parfen.blockappstudyrelease.ui.books.components

import android.content.Context
import android.content.res.Resources
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.parfen.blockappstudyrelease.R
import java.util.*

@Composable
fun BookHeader(
    language: String,
    age: String
) {
    val context = LocalContext.current
    val languageDisplayName = getLanguageDisplayName(context, language)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.language_label, languageDisplayName),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.age_label2, if (age.isBlank()) "?" else age),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// Получение полного названия языка из его кода
fun getLanguageDisplayName(context: Context, languageCode: String): String {
    return Locale(languageCode).getDisplayLanguage(Locale(languageCode)).replaceFirstChar { it.uppercase() }
}
