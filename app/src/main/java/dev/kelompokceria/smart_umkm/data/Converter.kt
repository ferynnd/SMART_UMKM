package dev.kelompokceria.smart_umkm.data

import androidx.room.TypeConverter
import java.util.Date

class Converter {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}
