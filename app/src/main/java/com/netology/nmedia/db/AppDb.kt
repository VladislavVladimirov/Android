package com.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.netology.nmedia.dao.Converters
import com.netology.nmedia.dao.PostDao
import com.netology.nmedia.entity.PostEntity


@Database(entities = [PostEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao

}

