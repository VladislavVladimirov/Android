package com.netology.nmedia.util

import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Formatter {
    fun formatCount(count: Int): String? {
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

            in 1000000..999999999 -> {
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.DOWN
                val result = df.format(count / 1000000.0)
                return result.toString() + "M"
            }
        }
        return null
    }

    fun formatTime(input: String): String {
        val postTime = ZonedDateTime.parse(input, DateTimeFormatter.ISO_OFFSET_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault())
        val fullDate = DateTimeFormatter.ofPattern("dd MMMM yyyy в HH:mm")
            .format(postTime)
        val formattedDate = DateTimeFormatter.ofPattern("d MMMM в HH:mm")
            .format(postTime)
        val formattedMinutes = DateTimeFormatter.ofPattern("в HH:mm")
            .format(postTime)
        val currentTime = LocalDateTime.now()
        when (currentTime.year - postTime.year) {
            0 -> {
                return when (currentTime.dayOfMonth - postTime.dayOfMonth) {
                    0 -> {
                        "Сегодня $formattedMinutes"
                    }

                    1 -> {
                        "Вчера $formattedMinutes"
                    }

                    else -> {
                        formattedDate
                    }
                }
            }

            else -> {
                return fullDate
            }

        }
    }
}