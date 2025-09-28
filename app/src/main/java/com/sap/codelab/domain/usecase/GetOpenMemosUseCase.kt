package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.utils.Result

/**
 * Use case for getting open (not completed) memos.
 * Follows Single Responsibility Principle - handles only getting open memos.
 * Includes proper error handling.
 */
class GetOpenMemosUseCase(
    private val memoRepository: MemoRepository
) {
    /**
     * Executes the use case to get open memos.
     * @return Result containing list of open memos or error
     */
    suspend operator fun invoke(): Result<List<MemoEntity>> {
        return memoRepository.getOpenMemos()
    }
}
