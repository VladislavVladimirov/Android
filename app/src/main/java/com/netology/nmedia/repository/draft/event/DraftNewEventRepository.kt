package com.netology.nmedia.repository.draft.event

interface DraftNewEventRepository {
    fun saveDraftContent(content: String)
    fun saveDraftDate(content: String)
    fun saveDraftTime(content: String)
    fun saveDraftFormat(content: String)
    fun saveDraftLink(content: String)
    fun getDraftContent(): String
    fun getDraftLink(): String
    fun getDraftDate():String
    fun getDraftTime():String
    fun getDraftFormat():String
    fun clearDrafts()
}