package pl.parfen.blockappstudyrelease.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "usage_logs",
    foreignKeys = [
        ForeignKey(
            entity = Profile::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UsageLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "profile_id")
    val profileId: Int,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "usage_time_seconds")
    val usageTimeSeconds: Int,

    @ColumnInfo(name = "blocked_time_seconds")
    val blockedTimeSeconds: Int,

    @ColumnInfo(name = "words_read")
    val wordsRead: Int
)
