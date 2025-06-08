package pl.parfen.blockappstudyrelease.data.repository.blockapp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.data.database.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Profile
import pl.parfen.blockappstudyrelease.data.model.UsageLog

class ProfileRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)

    suspend fun getProfileById(profileId: Int): Profile? = withContext(Dispatchers.IO) {
        db.profileDao().getProfileById(profileId)
    }

    suspend fun updateProfile(profile: Profile) = withContext(Dispatchers.IO) {
        db.profileDao().update(profile)
    }

    suspend fun insertUsageLog(usageLog: UsageLog) = withContext(Dispatchers.IO) {
        db.usageLogDao().insert(usageLog)
    }

    suspend fun getUsageLogForDay(profileId: Int, date: String): UsageLog? = withContext(Dispatchers.IO) {
        db.usageLogDao().getUsageLogForDay(profileId, date)
    }
}
