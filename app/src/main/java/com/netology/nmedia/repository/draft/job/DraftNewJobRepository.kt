package com.netology.nmedia.repository.draft.job

interface DraftNewJobRepository {
    fun saveDraftJobName(name: String)
    fun saveDraftJobPosition(position: String)
    fun saveDraftJobStart(start: String)
    fun saveDraftJobFinish(finish: String)
    fun saveDraftJobLink(link: String)
    fun getDraftJobName(): String
    fun getDraftJobPosition(): String
    fun getDraftJobStart(): String
    fun getDraftJobFinish(): String
    fun getDraftJobLink(): String
    fun clearDrafts()
}