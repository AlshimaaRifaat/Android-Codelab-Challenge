package com.sap.codelab.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sap.codelab.model.Memo

/**
 * That database that is used to store information.
 */
@Database(entities = [Memo::class], version = 2, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract fun getMemoDao(): MemoDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    // Step 1: Create new table with correct column types
                    db.execSQL("""
                        CREATE TABLE memo_new (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            title TEXT NOT NULL,
                            description TEXT NOT NULL,
                            reminderDate INTEGER NOT NULL,
                            reminderLatitude REAL NOT NULL DEFAULT 0.0,
                            reminderLongitude REAL NOT NULL DEFAULT 0.0,
                            isDone INTEGER NOT NULL DEFAULT 0
                        )
                    """.trimIndent())
                    
                    // Step 2: Copy data from old table to new table with safe conversion
                    db.execSQL("""
                        INSERT INTO memo_new (id, title, description, reminderDate, reminderLatitude, reminderLongitude, isDone)
                        SELECT 
                            id, 
                            title, 
                            description, 
                            reminderDate, 
                            CASE 
                                WHEN reminderLatitude IS NULL OR reminderLatitude = 0 THEN 0.0
                                ELSE CAST(reminderLatitude AS REAL)
                            END,
                            CASE 
                                WHEN reminderLongitude IS NULL OR reminderLongitude = 0 THEN 0.0
                                ELSE CAST(reminderLongitude AS REAL)
                            END,
                            isDone
                        FROM memo
                    """.trimIndent())
                    
                    // Step 3: Drop old table
                    db.execSQL("DROP TABLE memo")
                    
                    // Step 4: Rename new table to original name
                    db.execSQL("ALTER TABLE memo_new RENAME TO memo")
                    
                } catch (e: Exception) {
                    // Log the error for debugging
                    android.util.Log.e("DatabaseMigration", "Migration failed: ${e.message}")
                    throw e
                }
            }
        }
    }
}