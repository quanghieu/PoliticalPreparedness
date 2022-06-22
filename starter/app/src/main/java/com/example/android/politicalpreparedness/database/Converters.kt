package com.example.android.politicalpreparedness.database

import androidx.room.TypeConverter
import com.example.android.politicalpreparedness.network.models.Office
import com.example.android.politicalpreparedness.network.models.Official
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun officeToString(office: Office): String? {
        return office.toString()
    }

    @TypeConverter
    fun officialToString(official: Official) : String? {
        return official.toString()
    }
}