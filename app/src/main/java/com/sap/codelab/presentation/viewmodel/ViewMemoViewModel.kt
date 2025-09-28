package com.sap.codelab.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.domain.usecase.GetMemoByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the ViewMemo screen.
 * Handles displaying memo details.
 */
class ViewMemoViewModel(
    private val getMemoByIdUseCase: GetMemoByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewMemoUiState())
    val uiState: StateFlow<ViewMemoUiState> = _uiState.asStateFlow()

    /**
     * Loads a memo by its ID.
     */
    fun loadMemo(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getMemoByIdUseCase(id)
                .onSuccess { memo ->
                    _uiState.value = _uiState.value.copy(
                        memo = memo,
                        isLoading = false,
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
     * Clears any error messages.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI state for the ViewMemo screen.
 */
data class ViewMemoUiState(
    val memo: MemoEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
