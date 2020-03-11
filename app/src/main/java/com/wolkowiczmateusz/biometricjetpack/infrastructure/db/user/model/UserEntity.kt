package com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    @ColumnInfo(name = "local_id")
    var localId: UUID,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "password")
    var password: String
)