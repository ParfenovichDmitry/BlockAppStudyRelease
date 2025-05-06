# --- Сохраняем классы Room (БД)
-keep class androidx.room.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# --- Оставляем Model классы (Gson)
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Сохраняем модели данных (например, Book)
-keep class pl.parfen.blockappstudyrelease.data.model.** { *; }

# --- Для Apache POI (DOC, DOCX)
-keep class org.apache.poi.** { *; }
-dontwarn org.apache.poi.**

# --- Для iText PDF
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# --- Для Jsoup (EPUB чтение HTML)
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# --- Для Coil (загрузка изображений)
-keep class coil.** { *; }
-dontwarn coil.**

# --- Для Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# --- Для Compose (UI)
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# --- Общие настройки безопасности
-dontwarn javax.annotation.**
-dontwarn sun.misc.**

# --- Обработка Kotlin Metadata
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# --- Для Google ML Kit (если OCR используется)
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
