package pl.parfen.blockappstudyrelease.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.parfen.blockappstudyrelease.data.model.UsageLog

@Dao
interface UsageLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usageLog: UsageLog)


    @Query("SELECT * FROM usage_logs WHERE profile_id = :profileId")
    suspend fun getUsageLogsForProfile(profileId: Int): List<UsageLog>


    @Query("SELECT * FROM usage_logs WHERE profile_id = :profileId AND date = :date")
    suspend fun getUsageLogForDay(profileId: Int, date: String): UsageLog?

    @Query("SELECT * FROM usage_logs WHERE profile_id = :profileId AND date BETWEEN :startDate AND :endDate")
    suspend fun getUsageLogsForPeriod(
        profileId: Int,
        startDate: String,
        endDate: String
    ): List<UsageLog>
}
