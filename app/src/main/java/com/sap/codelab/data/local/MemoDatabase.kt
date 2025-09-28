package com.sap.codelab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sap.codelab.data.model.Memo

/**
 * Room database for the app.
 * Contains all entities and provides access to DAOs.
 */
@Database(
    entities = [Memo::class],
    version = 1,
    exportSchema = false
)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
}
