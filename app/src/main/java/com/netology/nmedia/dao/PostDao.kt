package com.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.netology.MyApp.R
import com.netology.nmedia.entity.PostEntity
import com.netology.nmedia.viewmodel.PostFormatter.syncTime



@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)

    @Query(
        """
        UPDATE PostEntity SET 
        content = :content 
        WHERE id = :id
    """
    )
    fun updateContentById(id: Long, content: String)
    @Query(
        """
            UPDATE PostEntity SET
              published = :time 
        WHERE id = :id
        """
    )
    fun updateTimeByid(id: Long, time: String)

    fun save(post: PostEntity) {
        post.published = syncTime()
        post.authorAvatar = R.drawable.ic_launcher_foreground
        post.author = "Нетология. Университет интернет-профессий будущего"
        if (post.id == 0L) insert(post) else
            updateTimeByid(post.id, post.published)
            updateContentById(post.id, post.content)
    }

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    fun likeById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        views = views + 1
        WHERE id = :id
    """
    )
    fun viewPostById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        shares = shares + 1
        WHERE id = :id
    """
    )
    fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)
}