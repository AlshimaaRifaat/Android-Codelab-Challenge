package com.sap.codelab.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sap.codelab.data.model.Memo
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Memo operations.
 * Handles all database operations for memos.
 */
@Dao
interface MemoDao {

    /**
     * Gets all memos from the database.
     */
    @Query("SELECT * FROM memo")
    suspend fun getAll(): List<Memo>

    /**
     * Gets all open (not completed) memos.
     */
    @Query("SELECT * FROM memo WHERE isDone = 0")
    suspend fun getOpen(): List<Memo>

    /**
     * Gets a memo by its ID.
     */
    @Query("SELECT * FROM memo WHERE id = :memoId")
    suspend fun getMemoById(memoId: Long): Memo?

    /**
     * Inserts a memo into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memo: Memo)

    /**
     * Marks a memo as done.
     */
    @Query("UPDATE memo SET isDone = 1 WHERE id = :memoId")
    suspend fun markAsDone(memoId: Long)

    /**
     * Observes all memos for real-time updates.
     */
    @Query("SELECT * FROM memo")
    fun observeAll(): Flow<List<Memo>>

    /**
     * Observes open memos for real-time updates.
     */
    @Query("SELECT * FROM memo WHERE isDone = 0")
    fun observeOpen(): Flow<List<Memo>>
}
