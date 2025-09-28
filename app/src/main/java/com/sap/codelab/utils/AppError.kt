package com.sap.codelab.utils

/**
 * Sealed class representing all possible errors in the app.
 * Follows Android best practices for error handling.
 */
sealed class AppError : Exception() {
    
    /**
     * Network-related errors
     */
    sealed class NetworkError : AppError() {
        object NoInternetConnection : NetworkError()
        object Timeout : NetworkError()
        object ServerError : NetworkError()
        data class HttpError(val code: Int, override val message: String) : NetworkError()
    }
    
    /**
     * Database-related errors
     */
    sealed class DatabaseError : AppError() {
        object DatabaseCorrupted : DatabaseError()
        object DatabaseLocked : DatabaseError()
        object DatabaseFull : DatabaseError()
        object ConstraintViolation : DatabaseError()
        data class QueryFailed(override val message: String) : DatabaseError()
    }
    
    /**
     * Validation errors
     */
    sealed class ValidationError : AppError() {
        object EmptyTitle : ValidationError()
        object EmptyDescription : ValidationError()
        object InvalidLocation : ValidationError()
        object TitleTooLong : ValidationError()
        object DescriptionTooLong : ValidationError()
        data class CustomValidationError(override val message: String) : ValidationError()
    }
    
    /**
     * Business logic errors
     */
    sealed class BusinessError : AppError() {
        object MemoNotFound : BusinessError()
        object MemoAlreadyCompleted : BusinessError()
        object InvalidOperation : BusinessError()
        object PermissionDenied : BusinessError()
    }
    
    /**
     * System errors
     */
    sealed class SystemError : AppError() {
        object OutOfMemory : SystemError()
        object StorageFull : SystemError()
        object LocationServiceUnavailable : SystemError()
        object PermissionDenied : SystemError()
        data class UnknownError(override val message: String) : SystemError()
    }
    
    /**
     * Gets a user-friendly error message
     */
    fun getUserMessage(): String = when (this) {
        is NetworkError.NoInternetConnection -> "No internet connection. Please check your network settings."
        is NetworkError.Timeout -> "Request timed out. Please try again."
        is NetworkError.ServerError -> "Server error. Please try again later."
        is NetworkError.HttpError -> "Network error: ${message}"
        
        is DatabaseError.DatabaseCorrupted -> "Database is corrupted. Please reinstall the app."
        is DatabaseError.DatabaseLocked -> "Database is locked. Please try again."
        is DatabaseError.DatabaseFull -> "Storage is full. Please free up some space."
        is DatabaseError.ConstraintViolation -> "Invalid data. Please check your input."
        is DatabaseError.QueryFailed -> "Database error: ${message}"
        
        is ValidationError.EmptyTitle -> "Title cannot be empty."
        is ValidationError.EmptyDescription -> "Description cannot be empty."
        is ValidationError.InvalidLocation -> "Invalid location data."
        is ValidationError.TitleTooLong -> "Title is too long. Maximum 100 characters allowed."
        is ValidationError.DescriptionTooLong -> "Description is too long. Maximum 500 characters allowed."
        is ValidationError.CustomValidationError -> message
        
        is BusinessError.MemoNotFound -> "Memo not found."
        is BusinessError.MemoAlreadyCompleted -> "Memo is already completed."
        is BusinessError.InvalidOperation -> "Invalid operation."
        is BusinessError.PermissionDenied -> "Permission denied."
        
        is SystemError.OutOfMemory -> "Out of memory. Please restart the app."
        is SystemError.StorageFull -> "Storage is full. Please free up some space."
        is SystemError.LocationServiceUnavailable -> "Location services are unavailable."
        is SystemError.PermissionDenied -> "Permission denied."
        is SystemError.UnknownError -> "An unexpected error occurred: ${message}"
    }
}
