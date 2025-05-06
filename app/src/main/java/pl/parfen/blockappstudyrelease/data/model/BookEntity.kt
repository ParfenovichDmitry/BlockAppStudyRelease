package pl.parfen.blockappstudyrelease.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_books")
data class BookEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val file: String,
    val language: String,
    val ageGroup: String?,
    val author: String,
    val isUserBook: Boolean,
    val storageType: StorageType,
    val pages: Int,
    val progress: Float,
    val fileUri: String?
)
