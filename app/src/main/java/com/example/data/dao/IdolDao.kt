package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.IdolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IdolDao {

    @Query("SELECT * FROM salon_idolos ORDER BY fechaRegistro DESC")
    fun getAllIdols(): Flow<List<IdolEntity>>

    @Query("SELECT * FROM salon_idolos WHERE id = :id LIMIT 1")
    fun getIdolById(id: String): Flow<IdolEntity?>

    @Query("SELECT * FROM salon_idolos WHERE id = :id LIMIT 1")
    suspend fun getIdolByIdSync(id: String): IdolEntity?

    @Query("SELECT COUNT(*) FROM salon_idolos")
    suspend fun getIdolCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdol(idol: IdolEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdols(idols: List<IdolEntity>)

    @Update
    suspend fun updateIdol(idol: IdolEntity)

    @Delete
    suspend fun deleteIdol(idol: IdolEntity)

    @Query("DELETE FROM salon_idolos WHERE id = :id")
    suspend fun deleteIdolById(id: String)

    @Query("DELETE FROM salon_idolos")
    suspend fun clearAll()
}
