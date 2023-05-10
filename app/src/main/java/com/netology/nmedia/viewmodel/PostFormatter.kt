package com.netology.nmedia.viewmodel

import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
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
    fun syncTime() : String {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy в HH:mm")
        return LocalDateTime.now().format(formatter).toString()
    }
}