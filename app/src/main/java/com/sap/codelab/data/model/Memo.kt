package com.sap.codelab.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model representing a Memo in the database.
 * This is the Room entity for database operations.
 */
@Entity(tableName = "memo")
data class Memo(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "reminderDate")
    val reminderDate: Long,
    @ColumnInfo(name = "reminderLatitude")
    val reminderLatitude: Double,
    @ColumnInfo(name = "reminderLongitude")
    val reminderLongitude: Double,
    @ColumnInfo(name = "isDone")
    val isDone: Boolean = false
)
