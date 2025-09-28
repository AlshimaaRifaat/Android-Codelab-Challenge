package com.sap.codelab.presentation.viewmodel

import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.domain.usecase.SaveMemoUseCase
import com.sap.codelab.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateMemoViewModelTest {

    private lateinit var saveMemoUseCase: SaveMemoUseCase
    private lateinit var createMemoViewModel: CreateMemoViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        saveMemoUseCase = SaveMemoUseCase(createMockRepository())
        Dispatchers.setMain(testDispatcher)
        
        createMemoViewModel = CreateMemoViewModel(
            saveMemoUseCase = saveMemoUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have correct default values`() {
        // When
        val uiState = createMemoViewModel.uiState.value

        // Then
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isSaved)
        assertFalse(uiState.hasTitleError)
        assertFalse(uiState.hasDescriptionError)
        assertNull(uiState.error)
    }

    @Test
    fun `updateMemo should update current memo and validation state`() {
        // Given
        val title = "Test Title"
        val description = "Test Description"

        // When
        createMemoViewModel.updateMemo(title, description)

        // Then
        val uiState = createMemoViewModel.uiState.value
        assertFalse(uiState.hasTitleError)
        assertFalse(uiState.hasDescriptionError)
        assertTrue(createMemoViewModel.isMemoValid())
    }

    @Test
    fun `updateMemo should set title error for empty title`() {
        // Given
        val title = ""
        val description = "Valid Description"

        // When
        createMemoViewModel.updateMemo(title, description)

        // Then
        val uiState = createMemoViewModel.uiState.value
        assertTrue(uiState.hasTitleError)
        assertFalse(uiState.hasDescriptionError)
        assertFalse(createMemoViewModel.isMemoValid())
    }

    @Test
    fun `updateMemo should set description error for empty description`() {
        // Given
        val title = "Valid Title"
        val description = ""

        // When
        createMemoViewModel.updateMemo(title, description)

        // Then
        val uiState = createMemoViewModel.uiState.value
        assertFalse(uiState.hasTitleError)
        assertTrue(uiState.hasDescriptionError)
        assertFalse(createMemoViewModel.isMemoValid())
    }

    @Test
    fun `updateMemo should set both errors for empty title and description`() {
        // Given
        val title = ""
        val description = ""

        // When
        createMemoViewModel.updateMemo(title, description)

        // Then
        val uiState = createMemoViewModel.uiState.value
        assertTrue(uiState.hasTitleError)
        assertTrue(uiState.hasDescriptionError)
        assertFalse(createMemoViewModel.isMemoValid())
    }

    @Test
    fun `updateMemoWithLocation should update memo with location data`() {
        // Given
        val title = "Test Title"
        val description = "Test Description"
        val latitude = 40.7128
        val longitude = -74.0060

        // When
        createMemoViewModel.updateMemoWithLocation(title, description, latitude, longitude)

        // Then
        val uiState = createMemoViewModel.uiState.value
        assertFalse(uiState.hasTitleError)
        assertFalse(uiState.hasDescriptionError)
        assertTrue(createMemoViewModel.isMemoValid())
    }

    @Test
    fun `clearError should remove error from UI state`() {
        // Given
        createMemoViewModel.updateMemo("", "") // This will cause validation errors
        val uiStateWithError = createMemoViewModel.uiState.value
        assertTrue(uiStateWithError.hasTitleError || uiStateWithError.hasDescriptionError)

        // When
        createMemoViewModel.clearError()

        // Then
        val uiState = createMemoViewModel.uiState.value
        assertNull(uiState.error)
    }

    @Test
    fun `isMemoValid should return true for valid memo`() {
        // Given
        val title = "Valid Title"
        val description = "Valid Description"

        // When
        createMemoViewModel.updateMemo(title, description)

        // Then
        assertTrue(createMemoViewModel.isMemoValid())
    }

    @Test
    fun `isMemoValid should return false for invalid memo`() {
        // Given
        val title = ""
        val description = ""

        // When
        createMemoViewModel.updateMemo(title, description)

        // Then
        assertFalse(createMemoViewModel.isMemoValid())
    }

    @Test
    fun `updateMemo should handle whitespace only input`() {
        // Given
        val title = "   "
        val description = "   "

        // When
        createMemoViewModel.updateMemo(title, description)

        // Then
        val uiState = createMemoViewModel.uiState.value
        assertTrue(uiState.hasTitleError)
        assertTrue(uiState.hasDescriptionError)
        assertFalse(createMemoViewModel.isMemoValid())
    }

    @Test
    fun `updateMemoWithLocation should handle valid location coordinates`() {
        // Given
        val title = "Location Memo"
        val description = "Description"
        val latitude = 40.7128
        val longitude = -74.0060

        // When
        createMemoViewModel.updateMemoWithLocation(title, description, latitude, longitude)

        // Then
        val uiState = createMemoViewModel.uiState.value
        assertFalse(uiState.hasTitleError)
        assertFalse(uiState.hasDescriptionError)
        assertTrue(createMemoViewModel.isMemoValid())
    }

    private fun createMockRepository() = object : com.sap.codelab.domain.repository.MemoRepository {
        override suspend fun saveMemo(memo: MemoEntity): Result<Unit> = Result.success(Unit)
        override suspend fun getAllMemos(): Result<List<MemoEntity>> = Result.success(emptyList())
        override suspend fun getOpenMemos(): Result<List<MemoEntity>> = Result.success(emptyList())
        override suspend fun getMemoById(id: Long): Result<MemoEntity> = Result.success(createMockMemo(id))
        override suspend fun markMemoAsDone(id: Long): Result<Unit> = Result.success(Unit)
        override suspend fun deleteMemo(id: Long): Result<Unit> = Result.success(Unit)
        override suspend fun updateMemo(memo: MemoEntity): Result<Unit> = Result.success(Unit)
        override fun observeAllMemos() = kotlinx.coroutines.flow.flowOf(Result.success(emptyList<MemoEntity>()))
        override fun observeOpenMemos() = kotlinx.coroutines.flow.flowOf(Result.success(emptyList<MemoEntity>()))
    }

    private fun createMockMemo(id: Long = 1L): MemoEntity {
        return MemoEntity(
            id = id,
            title = "Test Memo",
            description = "Test Description",
            isDone = false,
            reminderLatitude = 0.0,
            reminderLongitude = 0.0,
            reminderDate = System.currentTimeMillis()
        )
    }
}
