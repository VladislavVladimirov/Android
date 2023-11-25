package com.netology.nmedia.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.netology.nmedia.R
import java.util.regex.Matcher
import java.util.regex.Pattern

object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun extractUrls(text: String): List<String> {
        var containedUrls: List<String> = emptyList<String>()
        val urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"
        val pattern: Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
        val urlMatcher: Matcher = pattern.matcher(text)
        while (urlMatcher.find()) {
            containedUrls = containedUrls + text.substring(urlMatcher.start(0),urlMatcher.end(0))

        }
        return containedUrls
    }
    fun loadYouTubePreview(text: String, view: ImageView) {
        extractUrls(text) // Ищем первую ссылку на ютуб
            .find {
                it.contains("youtu")
            }
            // Если нашли
            ?.also {
                lateinit var videoId: String
                view.visibility = View.VISIBLE
                try {
                    if (it.contains("youtu.be")) {
                        videoId = it.split(".be/", "?")[1]
                        val videoPreviewUrl =
                            "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
                        Glide.with(view)
                            .load(videoPreviewUrl)
                            .timeout(10_000)
                            .into(view)
                    }
                    videoId = it.split("v=")[1]
                    val videoPreviewUrl =
                        "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
                    Glide.with(view)
                        .load(videoPreviewUrl)
                        .timeout(10_000)
                        .into(view)
                } catch (e: Exception) {
                    println("Error!")
                }
            }
        // Если не нашли
            ?: run {
               view.visibility = View.GONE
            }
    }
    fun loadImage(url: String, imageView: ImageView) {
        Glide.with(imageView)
            .load(url)
            .timeout(10_000)
            .into(imageView)
    }
    fun loadAvatar(url: String, imageView: ImageView) {
        Glide.with(imageView)
            .load(url)
            .placeholder(R.drawable.ic_loading_100dp)
            .error(R.drawable.ic_avatar_placeholder)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .timeout(10_000)
            .into(imageView)
    }
}