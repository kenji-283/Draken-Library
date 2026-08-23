package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookNoteDao {
    @Query("SELECT * FROM book_notes WHERE bookId = :bookId ORDER BY pageNumber ASC, timestamp DESC")
    fun getNotesForBook(bookId: String): Flow<List<BookNoteEntity>>

    @Query("SELECT * FROM book_notes WHERE bookId = :bookId AND pageNumber = :pageNumber ORDER BY timestamp DESC")
    fun getNotesForPage(bookId: String, pageNumber: Int): Flow<List<BookNoteEntity>>

    @Query("SELECT * FROM book_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<BookNoteEntity>>

    @Query("SELECT COUNT(*) FROM book_notes")
    suspend fun getNoteCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: BookNoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<BookNoteEntity>)

    @Update
    suspend fun updateNote(note: BookNoteEntity)

    @Delete
    suspend fun deleteNote(note: BookNoteEntity)

    @Query("DELETE FROM book_notes WHERE id = :id")
    suspend fun deleteNoteById(id: String)

    @Query("DELETE FROM book_notes WHERE bookId = :bookId")
    suspend fun deleteNotesByBookId(bookId: String)
}
