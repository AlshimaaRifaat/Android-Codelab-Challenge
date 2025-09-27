package com.sap.codelab.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.sap.codelab.model.Memo

/**
 * The repository is used to retrieve data from a data source.
 */
object Repository : IMemoRepository {

    private lateinit var database: Database

    fun initialize(applicationContext: Context) {
        database = DatabaseManager.initialize(applicationContext)
    }

    @WorkerThread
    override fun saveMemo(memo: Memo) {
        database.getMemoDao().insert(memo)
    }

    @WorkerThread
    override fun getOpen(): List<Memo> = database.getMemoDao().getOpen()

    @WorkerThread
    override fun getAll(): List<Memo> = database.getMemoDao().getAll()

    @WorkerThread
    override fun getAllMemos(): List<Memo> = database.getMemoDao().getAll()

    @WorkerThread
    override fun getMemoById(id: Long): Memo = database.getMemoDao().getMemoById(id)

    @WorkerThread
    override fun markMemoAsDone(id: Long) = database.getMemoDao().markAsDone(id)
}