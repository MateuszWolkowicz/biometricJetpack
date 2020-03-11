package com.wolkowiczmateusz.biometricjetpack.login.data

import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.BiometricCryptoObject
import com.wolkowiczmateusz.biometricjetpack.model.User
import io.reactivex.Single

interface LoginRepository {
    fun login(email: String, password: String): Single<User>
    fun getRegisteredUser(): Single<User>
    fun getBiometricPrompt(): BiometricCryptoObject
    fun clearAllData()
    fun createBiometricKey()
    fun loginUserState(isLogIn: Boolean)
    fun isUserLogIn(): Boolean
}
