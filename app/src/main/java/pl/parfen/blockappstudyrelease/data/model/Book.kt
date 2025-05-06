package pl.parfen.blockappstudyrelease.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: Int,
    val title: String,
    val file: String,
    val language: String,

    @SerializedName("age_group")
    val ageGroup: String? = null,

    val author: String = "",
    val isUserBook: Boolean = false,
    val storageType: StorageType = StorageType.ASSETS,
    val pages: Int = 1,
    val progress: Float = 0f,
    val fileUri: String? = null
) : Parcelable
