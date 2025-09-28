package com.sap.codelab.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.domain.usecase.GetAllMemosUseCase
import com.sap.codelab.domain.usecase.GetOpenMemosUseCase
import com.sap.codelab.domain.usecase.MarkMemoAsDoneUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 * Handles UI state and business logic for displaying memos.
 */
class HomeViewModel(
    private val getAllMemosUseCase: GetAllMemosUseCase,
    private val getOpenMemosUseCase: GetOpenMemosUseCase,
    private val markMemoAsDoneUseCase: MarkMemoAsDoneUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Using lateinit for properties that will be initialized later
    private lateinit var currentFilter: String
    
    // Using lazy for expensive computations that are only needed when accessed
    private val _memoStatistics by lazy {
        calculateMemoStatistics()
    }
    
    private var isShowingAllMemos = false

    init {
        // Initialize lateinit property
        currentFilter = "open"
        loadOpenMemos()
    }

    /**
     * Loads all memos.
     */
    fun loadAllMemos() {
        isShowingAllMemos = true
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getAllMemosUseCase()
                .onSuccess { memos ->
                    _uiState.value = _uiState.value.copy(
                        memos = memos,
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
     * Loads open (not completed) memos.
     */
    fun loadOpenMemos() {
        isShowingAllMemos = false
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getOpenMemosUseCase()
                .onSuccess { memos ->
                    _uiState.value = _uiState.value.copy(
                        memos = memos,
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
     * Refreshes the current memo list.
     */
    fun refreshMemos() {
        if (isShowingAllMemos) {
            loadAllMemos()
        } else {
            loadOpenMemos()
        }
    }

    /**
     * Marks a memo as done.
     */
    fun markMemoAsDone(memo: MemoEntity) {
        viewModelScope.launch {
            markMemoAsDoneUseCase(memo.id)
                .onSuccess {
                    refreshMemos()
                }
                .onError { error ->
                    _uiState.value = _uiState.value.copy(error = error.getUserMessage())
                }
        }
    }

    /**
     * Clears any error messages.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Calculates memo statistics (demonstrates lazy property usage).
     */
    private fun calculateMemoStatistics(): Map<String, Int> {
        val memos = _uiState.value.memos
        return mapOf(
            "total" to memos.size,
            "completed" to memos.count { it.isDone },
            "pending" to memos.count { !it.isDone },
            "withLocation" to memos.count { it.hasLocation() }
        )
    }

}

/**
 * UI state for the Home screen.
 */
data class HomeUiState(
    val memos: List<MemoEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
