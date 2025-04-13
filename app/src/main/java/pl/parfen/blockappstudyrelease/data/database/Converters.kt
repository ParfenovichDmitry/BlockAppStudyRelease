package pl.parfen.blockappstudyrelease.data.database



import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toStringList(data: String?): List<String>? {
        if (data.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(data, listType)
    }
}
