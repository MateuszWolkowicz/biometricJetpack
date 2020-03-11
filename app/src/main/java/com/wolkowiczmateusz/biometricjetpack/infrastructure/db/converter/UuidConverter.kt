package com.wolkowiczmateusz.biometricjetpack.infrastructure.db.converter

import androidx.room.TypeConverter
import java.util.UUID

class UuidConverter {

    @TypeConverter
    fun uuidToString(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun stringToUuid(uuidString: String): UUID {
        return UUID.fromString(uuidString)
    }
}

