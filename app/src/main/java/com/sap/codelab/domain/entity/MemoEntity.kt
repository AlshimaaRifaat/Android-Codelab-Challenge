package com.sap.codelab.domain.entity

/**
 * Domain entity representing a Memo.
 * This is the core business entity that doesn't depend on any framework.
 */
data class MemoEntity(
    val id: Long = 0,
    val title: String,
    val description: String,
    val reminderDate: Long,
    val reminderLatitude: Double,
    val reminderLongitude: Double,
    val isDone: Boolean = false
) {
    /**
     * Validates if the memo has valid content.
     */
    fun isValid(): Boolean = title.isNotBlank() && description.isNotBlank()
    
    /**
     * Checks if the memo has location data.
     */
    fun hasLocation(): Boolean = reminderLatitude != 0.0 && reminderLongitude != 0.0
    
    /**
     * Creates a copy of the memo with updated completion status.
     */
    fun markAsDone(): MemoEntity = copy(isDone = true)
    
    /**
     * Creates a copy of the memo with updated location.
     */
    fun updateLocation(latitude: Double, longitude: Double): MemoEntity = 
        copy(reminderLatitude = latitude, reminderLongitude = longitude)
}
