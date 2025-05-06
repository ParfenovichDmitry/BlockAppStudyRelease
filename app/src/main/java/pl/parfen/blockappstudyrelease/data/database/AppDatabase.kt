package pl.parfen.blockappstudyrelease.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.parfen.blockappstudyrelease.data.database.dao.BookProgressDao
import pl.parfen.blockappstudyrelease.data.database.dao.BookDao
import pl.parfen.blockappstudyrelease.data.model.BookEntity
import pl.parfen.blockappstudyrelease.data.model.BookProgress
import pl.parfen.blockappstudyrelease.data.model.Profile
import pl.parfen.blockappstudyrelease.data.model.ProfileTypeConverters
import pl.parfen.blockappstudyrelease.data.model.UsageLog

@Database(
    entities = [
        Profile::class,
        BookProgress::class,
        UsageLog::class,
        BookEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(ProfileTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun bookProgressDao(): BookProgressDao

    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
