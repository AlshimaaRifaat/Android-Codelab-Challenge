package com.sap.codelab.utils

import com.sap.codelab.domain.entity.MemoEntity

/**
 * Validator for memo-related operations.
 * Follows single responsibility principle for validation logic.
 */
object MemoValidator {
    
    private const val MAX_TITLE_LENGTH = 100
    private const val MAX_DESCRIPTION_LENGTH = 500
    private const val MIN_TITLE_LENGTH = 1
    private const val MIN_DESCRIPTION_LENGTH = 1
    
    /**
     * Validates a memo entity
     */
    fun validateMemo(memo: MemoEntity): Result<MemoEntity> {
        return when (val titleResult = validateTitle(memo.title)) {
            is Result.Error -> titleResult
            is Result.Loading -> titleResult
            is Result.Success -> when (val descResult = validateDescription(memo.description)) {
                is Result.Error -> descResult
                is Result.Loading -> descResult
                is Result.Success -> when (val locationResult = validateLocation(memo.reminderLatitude, memo.reminderLongitude)) {
                    is Result.Error -> locationResult
                    is Result.Loading -> locationResult
                    is Result.Success -> Result.success(memo)
                }
            }
        }
    }
    
    /**
     * Validates memo title
     */
    fun validateTitle(title: String): Result<String> {
        return when {
            title.isBlank() -> Result.error(AppError.ValidationError.EmptyTitle)
            title.length < MIN_TITLE_LENGTH -> Result.error(AppError.ValidationError.EmptyTitle)
            title.length > MAX_TITLE_LENGTH -> Result.error(AppError.ValidationError.TitleTooLong)
            else -> Result.success(title.trim())
        }
    }
    
    /**
     * Validates memo description
     */
    fun validateDescription(description: String): Result<String> {
        return when {
            description.isBlank() -> Result.error(AppError.ValidationError.EmptyDescription)
            description.length < MIN_DESCRIPTION_LENGTH -> Result.error(AppError.ValidationError.EmptyDescription)
            description.length > MAX_DESCRIPTION_LENGTH -> Result.error(AppError.ValidationError.DescriptionTooLong)
            else -> Result.success(description.trim())
        }
    }
    
    /**
     * Validates location coordinates
     */
    fun validateLocation(latitude: Double, longitude: Double): Result<Pair<Double, Double>> {
        return when {
            latitude < -90 || latitude > 90 -> Result.error(AppError.ValidationError.InvalidLocation)
            longitude < -180 || longitude > 180 -> Result.error(AppError.ValidationError.InvalidLocation)
            else -> Result.success(Pair(latitude, longitude))
        }
    }
    
    /**
     * Validates memo ID
     */
    fun validateMemoId(id: Long): Result<Long> {
        return when {
            id <= 0 -> Result.error(AppError.BusinessError.MemoNotFound)
            else -> Result.success(id)
        }
    }
    
    /**
     * Sanitizes input string by removing dangerous characters
     */
    fun sanitizeInput(input: String): String {
        return input
            .trim()
            .replace(Regex("[<>\"'&]"), "") // Remove potentially dangerous characters
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
    }
    
    /**
     * Validates and sanitizes title
     */
    fun validateAndSanitizeTitle(title: String): Result<String> {
        val sanitized = sanitizeInput(title)
        return validateTitle(sanitized)
    }
    
    /**
     * Validates and sanitizes description
     */
    fun validateAndSanitizeDescription(description: String): Result<String> {
        val sanitized = sanitizeInput(description)
        return validateDescription(sanitized)
    }
}
