package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.utils.Result
import com.sap.codelab.utils.MemoValidator

/**
 * Use case for marking a memo as done.
 * Follows Single Responsibility Principle - handles only marking memos as done.
 * Includes proper error handling and validation.
 */
class MarkMemoAsDoneUseCase(
    private val memoRepository: MemoRepository
) {
    /**
     * Executes the use case to mark a memo as done.
     * @param id The ID of the memo to mark as done
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(id: Long): Result<Unit> {
        return when (val validationResult = MemoValidator.validateMemoId(id)) {
            is Result.Success -> memoRepository.markMemoAsDone(validationResult.data)
            is Result.Error -> validationResult
            is Result.Loading -> validationResult
        }
    }
}
