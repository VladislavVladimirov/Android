package com.netology.nmedia.repository.draft.post

interface DraftNewPostRepository {
    fun saveDraftContent(content: String)
    fun saveDraftLink(content: String)
    fun getDraftContent(): String
    fun getDraftLink(): String
    fun clearDrafts()
}