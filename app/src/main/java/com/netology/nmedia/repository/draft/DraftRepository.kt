package com.netology.nmedia.repository.draft

interface DraftRepository {
    fun saveDraft(content: String)
    fun getDraft(): String
    fun clearDraft()
}