package com.netology.nmedia.repository.draft.post

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DraftNewPostRepositoryImpl @Inject constructor(
    @ApplicationContext
    context: Context
) : DraftNewPostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo1", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(String::class.java).type

    private val keyContent = "draftContent"
    private val keyLink = "draftLink"

    private var draftContent = ""
    private var draftLink =""

    private val dataLink = MutableLiveData(draftLink)
    private val dataContent = MutableLiveData(draftContent)

    init {
        prefs.getString(keyContent, null)?.let {
            draftContent = gson.fromJson(it, type)
            dataContent.value = draftContent
        }
        prefs.getString(keyLink, null)?.let {
            draftLink = gson.fromJson(it, type)
            dataLink.value = draftLink
        }
    }

    override fun getDraftContent(): String {
        return draftContent
    }

    override fun getDraftLink(): String {
        return draftLink
    }

    override fun saveDraftContent(content: String) {
        draftContent = content
        dataContent.value = draftContent
        sync()
    }

    override fun saveDraftLink(content: String) {
        draftLink = content
        dataLink.value = draftLink
        sync()
    }

    override fun clearDrafts() {
        draftContent = ""
        draftLink = ""
    }
    private fun sync() {
        with(prefs.edit()) {
            putString(keyContent, gson.toJson(draftContent))
            putString(keyLink, gson.toJson(draftLink))
            apply()
        }
    }
}