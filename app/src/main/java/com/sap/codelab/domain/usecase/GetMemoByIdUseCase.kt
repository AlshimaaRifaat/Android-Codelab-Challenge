package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.utils.Result
import com.sap.codelab.utils.MemoValidator

/**
 * Use case for getting a memo by ID.
 * Follows Single Responsibility Principle - handles only getting a memo by ID.
 * Includes proper error handling and validation.
 */
class GetMemoByIdUseCase(
    private val memoRepository: MemoRepository
) {
    /**
     * Executes the use case to get a memo by ID.
     * @param id The ID of the memo to retrieve
     * @return Result containing memo or error
     */
    suspend operator fun invoke(id: Long): Result<MemoEntity> {
        return when (val validationResult = MemoValidator.validateMemoId(id)) {
            is Result.Success -> memoRepository.getMemoById(validationResult.data)
            is Result.Error -> validationResult
            is Result.Loading -> validationResult
        }
    }
}
