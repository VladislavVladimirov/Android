package com.netology.nmedia.repository.draft.event


import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DraftNewEventRepositoryImpl @Inject constructor(
    @ApplicationContext
    context: Context
) : DraftNewEventRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo3", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(String::class.java).type

    private val keyContent = "draftContent"
    private val keyLink = "draftLink"
    private val keyDate = "draftDate"
    private val keyTime = "draftTime"
    private val keyFormat = "draftFormat"

    private var draftContent = ""
    private var draftLink = ""
    private var draftDate = ""
    private var draftTime = ""
    private var draftFormat = ""

    private val dataLink = MutableLiveData(draftLink)
    private val dataContent = MutableLiveData(draftContent)
    private val dataDate = MutableLiveData(draftDate)
    private val dataTime = MutableLiveData(draftTime)
    private val dataFormat = MutableLiveData(draftFormat)

    init {
        prefs.getString(keyContent, null)?.let {
            draftContent = gson.fromJson(it, type)
            dataContent.value = draftContent
        }
        prefs.getString(keyLink, null)?.let {
            draftLink = gson.fromJson(it, type)
            dataLink.value = draftLink
        }
        prefs.getString(keyDate, null)?.let {
            draftDate = gson.fromJson(it, type)
            dataDate.value = draftDate
        }
        prefs.getString(keyTime, null)?.let {
            draftTime = gson.fromJson(it, type)
            dataTime.value = draftTime
        }
        prefs.getString(keyFormat, null)?.let {
            draftFormat = gson.fromJson(it, type)
            dataFormat.value = draftFormat
        }
    }

    override fun getDraftContent(): String {
        return draftContent
    }

    override fun getDraftLink(): String {
        return draftLink
    }

    override fun getDraftDate(): String {
        return draftDate
    }

    override fun getDraftTime(): String {
        return draftTime
    }

    override fun getDraftFormat(): String {
        return draftFormat
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

    override fun saveDraftDate(content: String) {
        draftDate = content
        dataDate.value = draftDate
        sync()
    }

    override fun saveDraftTime(content: String) {
       draftTime = content
        dataTime.value = draftTime
        sync()
    }

    override fun saveDraftFormat(content: String) {
        draftFormat = content
        dataFormat.value = draftFormat
        sync()
    }

    override fun clearDrafts() {
        draftContent = ""
        draftLink = ""
        draftDate = ""
        draftTime = ""
        draftFormat = ""
    }

    private fun sync() {
        with(prefs.edit()) {
            putString(keyContent, gson.toJson(draftContent))
            putString(keyLink, gson.toJson(draftLink))
            putString(keyDate, gson.toJson(draftDate))
            putString(keyTime, gson.toJson(draftTime))
            putString(keyFormat, gson.toJson(draftFormat))
            apply()
        }
    }
}