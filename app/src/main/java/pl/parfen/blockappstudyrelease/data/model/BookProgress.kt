package pl.parfen.blockappstudyrelease.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_progress")
data class BookProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "profile_id")
    val profileId: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "progress")
    val progress: Double,

    @ColumnInfo(name = "file")
    val file: String,

    @ColumnInfo(name = "language")
    val language: String = "user",

    @ColumnInfo(name = "file_uri")
    val fileUri: String? = null
)
