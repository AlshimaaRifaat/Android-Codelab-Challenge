package com.sap.codelab.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sap.codelab.BuildConfig

/**
 * Production-ready database manager with comprehensive migration handling.
 */
object DatabaseManager {
    
    private const val TAG = "DatabaseManager"
    
    private var database: Database? = null
    
    /**
     * Initialize the database with proper migration handling.
     */
    internal fun initialize(context: Context): Database {
        if (database == null) {
            synchronized(this) {
                if (database == null) {
                    database = createDatabase(context)
                }
            }
        }
        return database!!
    }
    
    private fun createDatabase(context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, BuildConfig.DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration(true) // For development only
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d(TAG, "Database created successfully")
                }
                
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Log.d(TAG, "Database opened successfully")
                }
            })
            .build()
    }
    
    /**
     * Migration from version 1 to 2: Convert latitude/longitude from Long to Double
     */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            Log.d(TAG, "Starting migration from version 1 to 2")
            
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
                
                // Step 2: Copy data with safe conversion
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
                
                // Step 4: Rename new table
                db.execSQL("ALTER TABLE memo_new RENAME TO memo")
                
                Log.d(TAG, "Migration completed successfully")
                
            } catch (e: Exception) {
                Log.e(TAG, "Migration failed: ${e.message}", e)
                throw RuntimeException("Database migration failed", e)
            }
        }
    }
    
    /**
     * Get the database instance. Must be called after initialize().
     */
    internal fun getDatabase(): Database {
        return database ?: throw IllegalStateException("Database not initialized. Call initialize() first.")
    }
    
    /**
     * Close the database connection.
     */
    internal fun close() {
        database?.close()
        database = null
    }
}
