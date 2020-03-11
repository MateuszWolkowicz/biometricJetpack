package com.wolkowiczmateusz.biometricjetpack

import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.model.UserEntity
import com.wolkowiczmateusz.biometricjetpack.model.User
import java.util.UUID

const val ANY_ERROR = "Any error !"

val NEW_VALID_USER = User(
    name = "testName"
)

val NEW_EMPTY_USER = User(
    name = null
)

val NEW_EMPTY_USER_ENTITY = UserEntity(
    localId = UUID.randomUUID(),
    email = "testName",
    password = "encryptedPassword"
)