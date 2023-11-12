package com.netology.nmedia.util

import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
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
        val fullDate = DateTimeFormatter.ofPattern("dd MMMM yyyy в HH:mm")
            .format(LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME))
        val formattedDate = DateTimeFormatter.ofPattern("d MMMM  в HH:mm")
            .format(LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME))
        val formatedMinutes = DateTimeFormatter.ofPattern("в HH:mm")
            .format(LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME))
        val inputDate = LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME)
        val currentTime = LocalDateTime.now()
        when (currentTime.year - inputDate.year) {
            0 -> {
                return when (currentTime.dayOfMonth - inputDate.dayOfMonth) {
                    0 -> {
                        "Сегодня $formatedMinutes"
                    }

                    1 -> {
                        "Вчера $formatedMinutes"
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