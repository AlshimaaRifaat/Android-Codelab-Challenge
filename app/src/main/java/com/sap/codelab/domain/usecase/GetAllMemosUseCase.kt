package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.utils.Result

/**
 * Use case for getting all memos.
 * Follows Single Responsibility Principle - handles only getting all memos.
 * Includes proper error handling.
 */
class GetAllMemosUseCase(
    private val memoRepository: MemoRepository
) {
    /**
     * Executes the use case to get all memos.
     * @return Result containing list of memos or error
     */
    suspend operator fun invoke(): Result<List<MemoEntity>> {
        return memoRepository.getAllMemos()
    }
}
