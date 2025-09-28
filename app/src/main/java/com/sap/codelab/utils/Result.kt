package com.sap.codelab.utils

/**
 * Result type for handling success and error states.
 * Follows functional programming principles for error handling.
 */
sealed class Result<out T> {
    
    /**
     * Success result containing data
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Error result containing error information
     */
    data class Error(val appError: AppError) : Result<Nothing>()
    
    /**
     * Loading state for async operations
     */
    object Loading : Result<Nothing>()
    
    /**
     * Checks if the result is successful
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Checks if the result is an error
     */
    val isError: Boolean get() = this is Error
    
    /**
     * Checks if the result is loading
     */
    val isLoading: Boolean get() = this is Loading
    
    /**
     * Gets the data if successful, null otherwise
     */
    fun getDataOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Gets the error if present, null otherwise
     */
    fun getErrorOrNull(): AppError? = when (this) {
        is Error -> appError
        else -> null
    }
    
    /**
     * Maps the success data to another type
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }
    
    /**
     * Maps the error to another error type
     */
    inline fun mapError(transform: (AppError) -> AppError): Result<T> = when (this) {
        is Success -> this
        is Error -> Error(transform(appError))
        is Loading -> this
    }
    
    /**
     * Executes action on success
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    /**
     * Executes action on error
     */
    inline fun onError(action: (AppError) -> Unit): Result<T> {
        if (this is Error) action(appError)
        return this
    }
    
    /**
     * Executes action on loading
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
    
    /**
     * Gets data or throws exception
     */
    fun getDataOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw appError
        is Loading -> throw IllegalStateException("Result is still loading")
    }
    
    companion object {
        /**
         * Creates a success result
         */
        fun <T> success(data: T): Result<T> = Success(data)
        
        /**
         * Creates an error result
         */
        fun <T> error(appError: AppError): Result<T> = Error(appError)
        
        /**
         * Creates a loading result
         */
        fun <T> loading(): Result<T> = Loading
    }
}
