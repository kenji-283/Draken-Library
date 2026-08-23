package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY dateAdded DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE category = :category ORDER BY title ASC")
    fun getBooksByCategory(category: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isDownloaded = 1 ORDER BY title ASC")
    fun getDownloadedBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: String): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookByIdSync(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query("UPDATE books SET rating = :rating WHERE id = :id")
    suspend fun updateRating(id: String, rating: Int)

    @Query("UPDATE books SET currentPage = :page, isFinished = :isFinished WHERE id = :id")
    suspend fun updateProgress(id: String, page: Int, isFinished: Boolean)

    @Query("UPDATE books SET isDownloaded = :isDownloaded, pdfPath = :pdfPath, fileSizeBytes = :fileSizeBytes WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean, pdfPath: String, fileSizeBytes: Long)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: String)

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    @Query("SELECT SUM(fileSizeBytes) FROM books WHERE isDownloaded = 1")
    fun getTotalStorageUsedBytes(): Flow<Long?>

    @Query("DELETE FROM books")
    suspend fun clearAll()
}
