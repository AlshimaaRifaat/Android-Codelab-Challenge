package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.utils.Result
import com.sap.codelab.utils.MemoValidator

/**
 * Use case for saving a memo.
 * Follows Single Responsibility Principle - handles only saving memos.
 * Includes proper error handling and validation.
 */
class SaveMemoUseCase(
    private val memoRepository: MemoRepository
) {
    /**
     * Executes the use case to save a memo.
     * @param memo The memo to save
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(memo: MemoEntity): Result<Unit> {
        return when (val validationResult = MemoValidator.validateMemo(memo)) {
            is Result.Success -> memoRepository.saveMemo(validationResult.data)
            is Result.Error -> validationResult
            is Result.Loading -> validationResult
        }
    }
}
