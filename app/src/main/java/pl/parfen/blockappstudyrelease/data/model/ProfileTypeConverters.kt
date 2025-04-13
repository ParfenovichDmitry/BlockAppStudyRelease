package pl.parfen.blockappstudyrelease.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ProfileTypeConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }
}
