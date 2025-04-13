package pl.parfen.blockappstudyrelease.data.database

import androidx.room.*
import pl.parfen.blockappstudyrelease.data.model.Profile

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile): Long

    @Query("SELECT * FROM profiles")
    suspend fun getAllProfiles(): List<Profile>

    @Query("SELECT * FROM profiles WHERE id = :profileId")
    suspend fun getProfileById(profileId: Int): Profile?

    @Query("SELECT blocked_apps FROM profiles WHERE id = :profileId")
    suspend fun getSelectedAppsForProfile(profileId: Int): String

    @Query("UPDATE profiles SET blocked_apps = :selectedApps WHERE id = :profileId")
    suspend fun updateSelectedApps(profileId: Int, selectedApps: String)

    @Update
    suspend fun update(profile: Profile)

    @Delete
    suspend fun delete(profile: Profile)
}
