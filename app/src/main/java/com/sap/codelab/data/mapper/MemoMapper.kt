package com.sap.codelab.data.mapper

import com.sap.codelab.data.model.Memo as DataMemo
import com.sap.codelab.domain.entity.MemoEntity

/**
 * Mapper class to convert between domain entities and data models.
 * Follows Single Responsibility Principle - handles only mapping between layers.
 */
object MemoMapper {
    
    /**
     * Maps a domain entity to a data model.
     */
    fun toDataModel(entity: MemoEntity): DataMemo {
        return DataMemo(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            reminderDate = entity.reminderDate,
            reminderLatitude = entity.reminderLatitude,
            reminderLongitude = entity.reminderLongitude,
            isDone = entity.isDone
        )
    }
    
    /**
     * Maps a data model to a domain entity.
     */
    fun toDomainEntity(dataModel: DataMemo): MemoEntity {
        return MemoEntity(
            id = dataModel.id,
            title = dataModel.title,
            description = dataModel.description,
            reminderDate = dataModel.reminderDate,
            reminderLatitude = dataModel.reminderLatitude,
            reminderLongitude = dataModel.reminderLongitude,
            isDone = dataModel.isDone
        )
    }
    
    /**
     * Maps a list of data models to domain entities.
     */
    fun toDomainEntities(dataModels: List<DataMemo>): List<MemoEntity> {
        return dataModels.map { toDomainEntity(it) }
    }
}
