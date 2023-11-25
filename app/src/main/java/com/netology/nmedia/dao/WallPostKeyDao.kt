package com.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netology.nmedia.entity.WallPostKeyEntity
@Dao
interface WallPostKeyDao {
    @Query("SELECT MAX('id') FROM WallPostKeyEntity")
    suspend fun max(): Int?

    @Query("SELECT MIN('id') FROM WallPostKeyEntity")
    suspend fun min(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keyEntity: WallPostKeyEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<WallPostKeyEntity>)
    @Query("DELETE FROM WallPostKeyEntity")
    suspend fun removeAll()
}