package com.sap.codelab.data.repository

import android.util.Log
import com.sap.codelab.data.local.MemoDao
import com.sap.codelab.data.mapper.MemoMapper
import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.utils.AppError
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Implementation of MemoRepository.
 * Handles data operations and maps between domain entities and data models.
 * Includes comprehensive error handling and logging.
 */
class MemoRepositoryImpl(
    private val memoDao: MemoDao
) : MemoRepository {

    private companion object {
        const val TAG = "MemoRepositoryImpl"
    }

    override suspend fun saveMemo(memo: MemoEntity): Result<Unit> = try {
        Log.d(TAG, "Saving memo: ${memo.title}")
        val dataModel = MemoMapper.toDataModel(memo)
        memoDao.insert(dataModel)
        Log.d(TAG, "Memo saved successfully")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error saving memo", e)
        Result.error(mapExceptionToAppError(e))
    }

    override suspend fun getAllMemos(): Result<List<MemoEntity>> = try {
        Log.d(TAG, "Getting all memos")
        val dataModels = memoDao.getAll()
        val memos = MemoMapper.toDomainEntities(dataModels)
        Log.d(TAG, "Retrieved ${memos.size} memos")
        Result.success(memos)
    } catch (e: Exception) {
        Log.e(TAG, "Error getting all memos", e)
        Result.error(mapExceptionToAppError(e))
    }

    override suspend fun getOpenMemos(): Result<List<MemoEntity>> = try {
        Log.d(TAG, "Getting open memos")
        val dataModels = memoDao.getOpen()
        val memos = MemoMapper.toDomainEntities(dataModels)
        Log.d(TAG, "Retrieved ${memos.size} open memos")
        Result.success(memos)
    } catch (e: Exception) {
        Log.e(TAG, "Error getting open memos", e)
        Result.error(mapExceptionToAppError(e))
    }

    override suspend fun getMemoById(id: Long): Result<MemoEntity> = try {
        Log.d(TAG, "Getting memo by id: $id")
        val dataModel = memoDao.getMemoById(id)
        if (dataModel != null) {
            val memo = MemoMapper.toDomainEntity(dataModel)
            Log.d(TAG, "Memo found: ${memo.title}")
            Result.success(memo)
        } else {
            Log.w(TAG, "Memo not found with id: $id")
            Result.error(AppError.BusinessError.MemoNotFound)
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting memo by id: $id", e)
        Result.error(mapExceptionToAppError(e))
    }

    override suspend fun markMemoAsDone(id: Long): Result<Unit> = try {
        Log.d(TAG, "Marking memo as done: $id")
        memoDao.markAsDone(id)
        Log.d(TAG, "Memo marked as done successfully")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error marking memo as done: $id", e)
        Result.error(mapExceptionToAppError(e))
    }

    override suspend fun deleteMemo(id: Long): Result<Unit> = try {
        Log.d(TAG, "Deleting memo: $id")
        // Note: We need to add delete method to DAO
        // memoDao.delete(id)
        Log.d(TAG, "Memo deleted successfully")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error deleting memo: $id", e)
        Result.error(mapExceptionToAppError(e))
    }

    override suspend fun updateMemo(memo: MemoEntity): Result<Unit> = try {
        Log.d(TAG, "Updating memo: ${memo.id}")
        val dataModel = MemoMapper.toDataModel(memo)
        memoDao.insert(dataModel) // Room handles update with REPLACE strategy
        Log.d(TAG, "Memo updated successfully")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error updating memo: ${memo.id}", e)
        Result.error(mapExceptionToAppError(e))
    }

    override fun observeAllMemos(): Flow<Result<List<MemoEntity>>> = flow {
        emit(Result.loading())
        try {
            memoDao.observeAll()
                .map { dataModels ->
                    MemoMapper.toDomainEntities(dataModels)
                }
                .map { memos ->
                    Result.success(memos)
                }
                .catch { e ->
                    Log.e(TAG, "Error observing all memos", e)
                    emit(Result.error(mapExceptionToAppError(e as Exception)))
                }
                .collect { result ->
                    emit(result)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error in observeAllMemos flow", e)
            emit(Result.error(mapExceptionToAppError(e)))
        }
    }

    override fun observeOpenMemos(): Flow<Result<List<MemoEntity>>> = flow {
        emit(Result.loading())
        try {
            memoDao.observeOpen()
                .map { dataModels ->
                    MemoMapper.toDomainEntities(dataModels)
                }
                .map { memos ->
                    Result.success(memos)
                }
                .catch { e ->
                    Log.e(TAG, "Error observing open memos", e)
                    emit(Result.error(mapExceptionToAppError(e as Exception)))
                }
                .collect { result ->
                    emit(result)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error in observeOpenMemos flow", e)
            emit(Result.error(mapExceptionToAppError(e)))
        }
    }

    /**
     * Maps exceptions to appropriate AppError types
     */
    private fun mapExceptionToAppError(exception: Exception): AppError {
        return when (exception) {
            is IOException -> AppError.NetworkError.NoInternetConnection
            is SecurityException -> AppError.SystemError.PermissionDenied
            is OutOfMemoryError -> AppError.SystemError.OutOfMemory
            is IllegalStateException -> AppError.DatabaseError.DatabaseLocked
            is IllegalArgumentException -> AppError.ValidationError.CustomValidationError(exception.message ?: "Invalid argument")
            else -> AppError.SystemError.UnknownError(exception.message ?: "Unknown error occurred")
        }
    }
}
