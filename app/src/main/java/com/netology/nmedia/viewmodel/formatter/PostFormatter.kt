package com.netology.nmedia.viewmodel.formatter

import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object PostFormatter {
    fun formatCount(count: Int):String? {
        when (count) {
            in 0..999 -> return count.toString()
            in 1000..9999 -> {
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.DOWN
                val result = df.format(count / 1000.0)
                return result.toString() + "K"
            }
            in 10000..999999 -> {
                val df = DecimalFormat("#")
                df.roundingMode = RoundingMode.DOWN
                val result = df.format(count / 1000.0)
                return result.toString() + "K"
            }
            in 1000000 .. 999999999 -> {
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.DOWN
                val result = df.format(count / 1000000.0)
                return result.toString() + "M"
            }
        }
        return null
    }
    fun formatTime(time: String): String {
        val dateTime = LocalDateTime.ofEpochSecond(time.toLong(),0, ZoneOffset.of("+03:00"))
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy в HH:mm")
        return dateTime.format(formatter).toString()
    }
}