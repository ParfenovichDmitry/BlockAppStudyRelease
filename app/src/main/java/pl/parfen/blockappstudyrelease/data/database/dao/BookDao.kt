package pl.parfen.blockappstudyrelease.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.parfen.blockappstudyrelease.data.model.BookEntity


@Dao
interface BookDao {

    @Query("SELECT * FROM system_books")
    suspend fun getAllSystemBooks(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSystemBooks(books: List<BookEntity>)

    @Query("DELETE FROM system_books")
    suspend fun clearSystemBooks()
}
