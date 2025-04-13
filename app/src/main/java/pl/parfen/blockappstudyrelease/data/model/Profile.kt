package pl.parfen.blockappstudyrelease.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "avatar")
    val avatar: String = "",

    @ColumnInfo(name = "nickname")
    val nickname: String = "",

    @ColumnInfo(name = "age")
    val age: Int = 0,

    @ColumnInfo(name = "selected_resource")
    val selectedResource: String = "",

    @ColumnInfo(name = "usage_time")
    val usageTime: Int = 1,

    @ColumnInfo(name = "percentage")
    val percentage: Int = 40,

    @ColumnInfo(name = "blocked_apps")
    val blockedApps: List<String> = emptyList(),

    @ColumnInfo(name = "books")
    val books: List<String> = emptyList(),

    @ColumnInfo(name = "active_book")
    val activeBook: String = "",

    @ColumnInfo(name = "ai_network")
    val aiNetwork: String = "",

    @ColumnInfo(name = "ai_topics")
    val aiTopics: List<String> = emptyList(),

    @ColumnInfo(name = "ai_language")
    val aiLanguage: String = "",

    @ColumnInfo(name = "profile_language")
    val profileLanguage: String = "",

    @ColumnInfo(name = "show_all_books")
    val showAllBooks: Boolean = false,

    @ColumnInfo(name = "additional_language")
    val additionalLanguage: String? = null,

    @ColumnInfo(name = "selected_topics")
    val selectedTopics: List<String> = emptyList(),

    @ColumnInfo(name = "total_words_read")
    val totalWordsRead: Int = 0,

    @ColumnInfo(name = "password")
    val password: String? = null
) : Parcelable
