package pl.parfen.blockappstudyrelease.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pl.parfen.blockappstudyrelease.data.model.BookProgress

@Dao
interface BookProgressDao {

    @Insert
    suspend fun insert(bookProgress: BookProgress): Long

    @Update
    suspend fun update(bookProgress: BookProgress)

    @Query("SELECT * FROM book_progress WHERE profile_id = :profileId")
    suspend fun getProgressForProfile(profileId: Int): List<BookProgress>

    @Query("SELECT * FROM book_progress WHERE profile_id = :profileId AND title = :title LIMIT 1")
    suspend fun getProgressForBook(profileId: Int, title: String): BookProgress?

    @Query("DELETE FROM book_progress WHERE profile_id = :profileId AND title = :title")
    suspend fun deleteBookProgress(profileId: Int, title: String)

    @Query("UPDATE book_progress SET progress = :progress WHERE profile_id = :profileId AND title = :title")
    suspend fun updateProgress(profileId: Int, title: String, progress: Double)

    @Query("UPDATE book_progress SET language = :language WHERE profile_id = :profileId AND title = :title")
    suspend fun updateLanguage(profileId: Int, title: String, language: String)

    @Query("DELETE FROM book_progress WHERE id = :id")
    suspend fun deleteById(id: Int)
    @Query("DELETE FROM book_progress WHERE profile_id = :profileId AND title = :title")
    fun deleteByProfileIdAndTitle(profileId: Int, title: String)
}
