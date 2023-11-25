package com.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.netology.nmedia.dao.JobDao
import com.netology.nmedia.dao.PostDao
import com.netology.nmedia.dao.PostKeyDao
import com.netology.nmedia.dao.UserDao
import com.netology.nmedia.dao.WallPostDao
import com.netology.nmedia.dao.WallPostKeyDao
import com.netology.nmedia.entity.JobEntity
import com.netology.nmedia.entity.PostKeyEntity
import com.netology.nmedia.entity.PostEntity
import com.netology.nmedia.entity.UserEntity
import com.netology.nmedia.entity.WallPostEntity
import com.netology.nmedia.entity.WallPostKeyEntity
import com.netology.nmedia.util.Converters


@Database(
    entities = [
        PostEntity::class,
        JobEntity::class,
        UserEntity::class,
        WallPostEntity::class,
        PostKeyEntity::class,
        WallPostKeyEntity::class,
    ], version = 2
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun jobDao(): JobDao
    abstract fun userDao(): UserDao
    abstract fun wallPostDao(): WallPostDao
    abstract fun postKeyDao(): PostKeyDao
    abstract fun wallPostKeyDao(): WallPostKeyDao

}


