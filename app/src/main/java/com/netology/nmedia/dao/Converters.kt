package com.netology.nmedia.dao

import androidx.room.TypeConverter
import com.netology.nmedia.enums.AttachmentType

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun fromListDto(list: List<Int>?): String {
        if (list == null) return ""
        return list.toString()
    }


    @TypeConverter
    fun toListDto(data: String?): List<Int>? {
        return if (data == "[]") emptyList()
        else {
            val substr = data?.substring(1, data.length - 1)
            substr?.split(", ")?.map { it.toInt() }
        }
    }
}