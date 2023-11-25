package com.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netology.nmedia.entity.PostKeyEntity
@Dao
interface PostKeyDao {
    @Query("SELECT max(id) FROM PostKeyEntity")
    suspend fun max(): Int?

    @Query("SELECT min(id) FROM PostKeyEntity")
    suspend fun min(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keyEntity: PostKeyEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<PostKeyEntity>)
    @Query("DELETE FROM PostKeyEntity")
    suspend fun removeAll()
}