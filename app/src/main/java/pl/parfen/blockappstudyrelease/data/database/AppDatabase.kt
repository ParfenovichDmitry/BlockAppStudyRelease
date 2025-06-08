package pl.parfen.blockappstudyrelease.data.database

import android.content.Context
import androidx.room.*
import pl.parfen.blockappstudyrelease.data.database.dao.BookDao
import pl.parfen.blockappstudyrelease.data.database.dao.BookProgressDao
import pl.parfen.blockappstudyrelease.data.local.dao.UsageLogDao
import pl.parfen.blockappstudyrelease.data.model.*

@Database(
    entities = [
        Profile::class,
        BookProgress::class,
        UsageLog::class,
        BookEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(ProfileTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun bookProgressDao(): BookProgressDao
    abstract fun bookDao(): BookDao
    abstract fun usageLogDao(): UsageLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )

                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
