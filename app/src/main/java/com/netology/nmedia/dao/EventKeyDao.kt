package com.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netology.nmedia.entity.EventKeyEntity
@Dao
interface EventKeyDao {
    @Query("SELECT max(id) FROM EventKeyEntity")
    suspend fun max(): Int?

    @Query("SELECT min(id) FROM EventKeyEntity")
    suspend fun min(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keyEntity: EventKeyEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<EventKeyEntity>)
    @Query("DELETE FROM EventKeyEntity")
    suspend fun removeAll()
}