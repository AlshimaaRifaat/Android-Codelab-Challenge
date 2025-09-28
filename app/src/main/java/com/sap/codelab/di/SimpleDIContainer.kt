package com.sap.codelab.di

import android.content.Context
import androidx.room.Room
import com.sap.codelab.data.local.MemoDao
import com.sap.codelab.data.local.MemoDatabase
import com.sap.codelab.data.repository.MemoRepositoryImpl
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.domain.usecase.GetAllMemosUseCase
import com.sap.codelab.domain.usecase.GetMemoByIdUseCase
import com.sap.codelab.domain.usecase.GetOpenMemosUseCase
import com.sap.codelab.domain.usecase.MarkMemoAsDoneUseCase
import com.sap.codelab.domain.usecase.SaveMemoUseCase
import com.sap.codelab.presentation.viewmodel.CreateMemoViewModel
import com.sap.codelab.presentation.viewmodel.HomeViewModel
import com.sap.codelab.presentation.viewmodel.ViewMemoViewModel

/**
 * Simple dependency injection container.
 * Provides manual dependency injection without external frameworks.
 */
object SimpleDIContainer {
    
    private var memoRepository: MemoRepository? = null
    private var memoDao: MemoDao? = null
    
    /**
     * Initializes the DI container with the application context.
     */
    fun initialize(context: Context) {
        val database = Room.databaseBuilder(
            context,
            MemoDatabase::class.java,
            "memo_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        memoDao = database.memoDao()
        memoRepository = MemoRepositoryImpl(memoDao!!)
    }
    
    /**
     * Gets the memo repository.
     */
    fun getMemoRepository(): MemoRepository {
        return memoRepository ?: throw IllegalStateException("DI container not initialized")
    }
    
    /**
     * Creates a HomeViewModel with all dependencies.
     */
    fun createHomeViewModel(): HomeViewModel {
        val repository = getMemoRepository()
        return HomeViewModel(
            getAllMemosUseCase = GetAllMemosUseCase(repository),
            getOpenMemosUseCase = GetOpenMemosUseCase(repository),
            markMemoAsDoneUseCase = MarkMemoAsDoneUseCase(repository)
        )
    }
    
    /**
     * Creates a CreateMemoViewModel with all dependencies.
     */
    fun createCreateMemoViewModel(): CreateMemoViewModel {
        val repository = getMemoRepository()
        return CreateMemoViewModel(
            saveMemoUseCase = SaveMemoUseCase(repository)
        )
    }
    
    /**
     * Creates a ViewMemoViewModel with all dependencies.
     */
    fun createViewMemoViewModel(): ViewMemoViewModel {
        val repository = getMemoRepository()
        return ViewMemoViewModel(
            getMemoByIdUseCase = GetMemoByIdUseCase(repository)
        )
    }
}
