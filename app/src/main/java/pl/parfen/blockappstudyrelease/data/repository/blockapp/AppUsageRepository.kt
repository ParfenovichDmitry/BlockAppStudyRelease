package pl.parfen.blockappstudyrelease.data.repository.blockapp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.data.database.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.UsageLog

class AppUsageRepository(context: Context) {
    private val usageLogDao = AppDatabase.getDatabase(context).usageLogDao()

    suspend fun insertUsageLog(log: UsageLog) = withContext(Dispatchers.IO) {
        usageLogDao.insert(log)
    }

    suspend fun getUsageLogsForProfile(profileId: Int): List<UsageLog> = withContext(Dispatchers.IO) {
        usageLogDao.getUsageLogsForProfile(profileId)
    }

    suspend fun getUsageLogForDay(profileId: Int, date: String): UsageLog? = withContext(Dispatchers.IO) {
        usageLogDao.getUsageLogForDay(profileId, date)
    }

    suspend fun getUsageLogsForPeriod(profileId: Int, startDate: String, endDate: String): List<UsageLog> =
        withContext(Dispatchers.IO) {
            usageLogDao.getUsageLogsForPeriod(profileId, startDate, endDate)
        }
}
