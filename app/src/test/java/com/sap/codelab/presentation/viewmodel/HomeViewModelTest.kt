package com.sap.codelab.presentation.viewmodel

import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.domain.usecase.GetAllMemosUseCase
import com.sap.codelab.domain.usecase.GetOpenMemosUseCase
import com.sap.codelab.domain.usecase.MarkMemoAsDoneUseCase
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
class HomeViewModelTest {

    private lateinit var getAllMemosUseCase: GetAllMemosUseCase
    private lateinit var getOpenMemosUseCase: GetOpenMemosUseCase
    private lateinit var markMemoAsDoneUseCase: MarkMemoAsDoneUseCase
    private lateinit var homeViewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        getAllMemosUseCase = GetAllMemosUseCase(createMockRepository())
        getOpenMemosUseCase = GetOpenMemosUseCase(createMockRepository())
        markMemoAsDoneUseCase = MarkMemoAsDoneUseCase(createMockRepository())
        
        Dispatchers.setMain(testDispatcher)
        
        homeViewModel = HomeViewModel(
            getAllMemosUseCase = getAllMemosUseCase,
            getOpenMemosUseCase = getOpenMemosUseCase,
            markMemoAsDoneUseCase = markMemoAsDoneUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have correct default values`() {
        // When
        val uiState = homeViewModel.uiState.value

        // Then
        assertTrue(uiState.memos.isEmpty())
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `clearError should remove error from UI state`() {
        // When
        homeViewModel.clearError()

        // Then
        assertNull(homeViewModel.uiState.value.error)
    }

    @Test
    fun `loadAllMemos should be callable`() {
        // When
        homeViewModel.loadAllMemos()

        // Then
        // Test passes if no exception is thrown
        assertTrue(true)
    }

    @Test
    fun `loadOpenMemos should be callable`() {
        // When
        homeViewModel.loadOpenMemos()

        // Then
        // Test passes if no exception is thrown
        assertTrue(true)
    }

    @Test
    fun `refreshMemos should be callable`() {
        // When
        homeViewModel.refreshMemos()

        // Then
        // Test passes if no exception is thrown
        assertTrue(true)
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

    private fun createMockMemo(
        id: Long = 1L,
        title: String = "Test Memo",
        description: String = "Test Description",
        isDone: Boolean = false,
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ): MemoEntity {
        return MemoEntity(
            id = id,
            title = title,
            description = description,
            isDone = isDone,
            reminderLatitude = latitude,
            reminderLongitude = longitude,
            reminderDate = System.currentTimeMillis()
        )
    }
}
