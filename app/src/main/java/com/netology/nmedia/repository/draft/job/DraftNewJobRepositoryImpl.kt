package com.netology.nmedia.repository.draft.job

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DraftNewJobRepositoryImpl @Inject constructor(
    @ApplicationContext
    context: Context
): DraftNewJobRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo2", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(String::class.java).type

    private val keyName = "draftName"
    private val keyPosition = "draftPosition"
    private val keyStart = "draftStart"
    private val keyFinish = "draftName"
    private val keyLink = "draftLink"

    private var draftName = ""
    private var draftPosition = ""
    private var draftStart = ""
    private var draftFinish = ""
    private var draftLink =""

    private val dataName = MutableLiveData(draftName)
    private val dataPosition = MutableLiveData(draftPosition)
    private val dataStart = MutableLiveData(draftStart)
    private val dataFinish = MutableLiveData(draftFinish)
    private val dataLink = MutableLiveData(draftLink)
    init {
        prefs.getString(keyName, null)?.let {
            draftName = gson.fromJson(it, type)
            dataName.value = draftName
        }
        prefs.getString(keyName, null)?.let {
            draftPosition = gson.fromJson(it, type)
            dataPosition.value = draftPosition
        }
        prefs.getString(keyName, null)?.let {
            draftStart = gson.fromJson(it, type)
            dataStart.value = draftStart
        }
        prefs.getString(keyName, null)?.let {
            draftFinish = gson.fromJson(it, type)
            dataFinish.value = draftFinish
        }
        prefs.getString(keyLink, null)?.let {
            draftLink = gson.fromJson(it, type)
            dataLink.value = draftLink
        }
    }
    override fun saveDraftJobName(name: String) {
        draftName = name
        dataName.value = draftName
        sync()
    }

    override fun saveDraftJobPosition(position: String) {
        draftPosition = position
        dataPosition.value = draftPosition
        sync()
    }

    override fun saveDraftJobStart(start: String) {
        draftStart = start
        dataStart.value = draftStart
        sync()
    }

    override fun saveDraftJobFinish(finish: String) {
        draftFinish = finish
        dataFinish.value = draftFinish
        sync()
    }

    override fun saveDraftJobLink(link: String) {
        draftLink = link
        dataLink.value = draftLink
        sync()
    }

    override fun getDraftJobName(): String {
       return draftName
    }

    override fun getDraftJobPosition(): String {
        return draftPosition
    }

    override fun getDraftJobStart(): String {
        return draftStart
    }

    override fun getDraftJobFinish(): String {
        return draftFinish
    }

    override fun getDraftJobLink(): String {
        return draftLink
    }

    override fun clearDrafts() {
        draftName = ""
        draftPosition = ""
        draftStart = ""
        draftFinish = ""
        draftLink = ""
    }
    private fun sync() {
        with(prefs.edit()) {
            putString(keyName, gson.toJson(draftName))
            putString(keyPosition, gson.toJson(draftPosition))
            putString(keyStart, gson.toJson(draftStart))
            putString(keyFinish, gson.toJson(draftFinish))
            putString(keyLink, gson.toJson(draftLink))
            apply()
        }
    }
}