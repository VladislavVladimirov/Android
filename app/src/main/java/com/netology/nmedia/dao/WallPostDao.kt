package com.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netology.nmedia.entity.WallPostEntity
@Dao
interface WallPostDao {
    @Query("SELECT * FROM WallPostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, WallPostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<WallPostEntity>)
    @Query("SELECT COUNT(*) == 0 FROM WallPostEntity")
    suspend fun isEmpty(): Boolean
    @Query("DELETE FROM WallPostEntity")
    suspend fun removeAll()

}