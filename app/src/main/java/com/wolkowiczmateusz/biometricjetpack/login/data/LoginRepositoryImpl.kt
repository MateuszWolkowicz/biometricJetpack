package com.wolkowiczmateusz.biometricjetpack.login.data

import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.OfflineDataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.model.UserEntity
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.BiometricCryptoObject
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.EncryptionServices
import com.wolkowiczmateusz.biometricjetpack.model.User
import com.wolkowiczmateusz.biometricjetpack.model.mappers.mapToUser
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val offlineDataSource: OfflineDataSource,
    private val encryptionService: EncryptionServices
): LoginRepository {

    override fun clearAllData() {
        offlineDataSource.deleteAllUsers()
        encryptionService.removeAllKeys()
    }

    override fun getRegisteredUser(): Single<User> {
        return offlineDataSource.getRegisteredUser()
            .map { it.mapToUser() }
    }

    override fun login(email: String, password: String): Single<User> {
        return offlineDataSource.getUserByEmail(email)
            .flatMap { userList ->
                val user: UserEntity
                user = if (userList.isEmpty()) {
                    createKeys(password)
                    val encryptedPassword = encryptionService.encrypt(password, password)
                    val newUserEntity =
                        UserEntity(
                            UUID.randomUUID(),
                            email = email,
                            password = encryptedPassword
                        )
                    offlineDataSource.insertUser(newUserEntity)
                    newUserEntity
                } else {
                    userList.first()
                }
                return@flatMap if (encryptionService.decrypt(user.password, password) == password) {
                    val newUser = user.mapToUser()
                    Single.just(newUser)
                } else {
                    Single.just(User())
                }
            }
    }

    private fun createKeys(password: String) {
        encryptionService.createMasterKey(password)
    }

    override fun createBiometricKey() {
        encryptionService.createBiometricKey()
    }

    override fun loginUserState(isLogIn: Boolean) {
        offlineDataSource.loginUserState(isLogIn)
    }

    override fun isUserLogIn(): Boolean {
        return offlineDataSource.isUserLogIn()
    }

    override fun getBiometricPrompt(): BiometricCryptoObject {
        return encryptionService.prepareBiometricCryptoObject()
    }
}
