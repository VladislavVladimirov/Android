package com.netology.nmedia.repository

interface DraftRepository {
    fun saveDraft(content: String)
    fun getDraft(): String
    fun clearDraft()
}