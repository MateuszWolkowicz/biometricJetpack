package com.wolkowiczmateusz.biometricjetpack.model.mappers

import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.model.UserEntity
import com.wolkowiczmateusz.biometricjetpack.model.User

fun UserEntity.mapToUser(): User {
    return User(name = this.email)
}