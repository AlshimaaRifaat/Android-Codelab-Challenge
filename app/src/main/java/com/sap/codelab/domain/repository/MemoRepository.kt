package com.sap.codelab.domain.repository

import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for memo operations.
 * This follows the Repository pattern and Dependency Inversion Principle.
 * Uses Result type for proper error handling.
 */
interface MemoRepository {
    
    /**
     * Saves a memo to the data source.
     * @param memo The memo to save
     * @return Result indicating success or failure
     */
    suspend fun saveMemo(memo: MemoEntity): Result<Unit>
    
    /**
     * Gets all memos from the data source.
     * @return Result containing list of memos or error
     */
    suspend fun getAllMemos(): Result<List<MemoEntity>>
    
    /**
     * Gets all open (not completed) memos.
     * @return Result containing list of open memos or error
     */
    suspend fun getOpenMemos(): Result<List<MemoEntity>>
    
    /**
     * Gets a memo by its ID.
     * @param id The ID of the memo to retrieve
     * @return Result containing memo or error
     */
    suspend fun getMemoById(id: Long): Result<MemoEntity>
    
    /**
     * Marks a memo as done.
     * @param id The ID of the memo to mark as done
     * @return Result indicating success or failure
     */
    suspend fun markMemoAsDone(id: Long): Result<Unit>
    
    /**
     * Deletes a memo.
     * @param id The ID of the memo to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteMemo(id: Long): Result<Unit>
    
    /**
     * Updates an existing memo.
     * @param memo The memo to update
     * @return Result indicating success or failure
     */
    suspend fun updateMemo(memo: MemoEntity): Result<Unit>
    
    /**
     * Observes all memos for real-time updates.
     * @return Flow of Result containing list of memos
     */
    fun observeAllMemos(): Flow<Result<List<MemoEntity>>>
    
    /**
     * Observes open memos for real-time updates.
     * @return Flow of Result containing list of open memos
     */
    fun observeOpenMemos(): Flow<Result<List<MemoEntity>>>
}
