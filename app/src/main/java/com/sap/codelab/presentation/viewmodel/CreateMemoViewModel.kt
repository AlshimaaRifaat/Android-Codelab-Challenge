package com.sap.codelab.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.domain.usecase.SaveMemoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the CreateMemo screen.
 * Handles memo creation logic and validation.
 */
class CreateMemoViewModel(
    private val saveMemoUseCase: SaveMemoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMemoUiState())
    val uiState: StateFlow<CreateMemoUiState> = _uiState.asStateFlow()

    private var currentMemo = MemoEntity(
        title = "",
        description = "",
        reminderDate = 0,
        reminderLatitude = 0.0,
        reminderLongitude = 0.0
    )

    /**
     * Updates the memo with new title and description.
     */
    fun updateMemo(title: String, description: String) {
        currentMemo = currentMemo.copy(
            title = title,
            description = description
        )
        _uiState.value = _uiState.value.copy(
            title = title,
            description = description,
            hasTitleError = title.isBlank(),
            hasDescriptionError = description.isBlank()
        )
    }

    /**
     * Updates the memo with location data.
     */
    fun updateMemoWithLocation(
        title: String,
        description: String,
        latitude: Double,
        longitude: Double
    ) {
        currentMemo = currentMemo.copy(
            title = title,
            description = description,
            reminderLatitude = latitude,
            reminderLongitude = longitude
        )
        _uiState.value = _uiState.value.copy(
            title = title,
            description = description,
            hasTitleError = title.isBlank(),
            hasDescriptionError = description.isBlank()
        )
    }

    /**
     * Saves the current memo.
     */
    fun saveMemo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            saveMemoUseCase(currentMemo)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSaved = true,
                        error = null
                    )
                }
                .onError { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.getUserMessage()
                    )
                }
        }
    }

    /**
     * Checks if the current memo is valid.
     */
    fun isMemoValid(): Boolean = currentMemo.isValid()

    /**
     * Clears any error messages.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI state for the CreateMemo screen.
 */
data class CreateMemoUiState(
    val title: String = "",
    val description: String = "",
    val hasTitleError: Boolean = false,
    val hasDescriptionError: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
