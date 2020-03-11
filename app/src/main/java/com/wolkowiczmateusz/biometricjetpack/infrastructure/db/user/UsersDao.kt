package com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.model.UserEntity
import io.reactivex.Single

@Dao
interface UsersDao {

    @Query("SELECT * FROM users where email = :email")
    fun getUserByEmail(email: String): Single<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userEntity: UserEntity)

    @Update
    fun update(userEntity: UserEntity)

    @Delete
    fun delete(userEntity: UserEntity)

    @Query("SELECT * FROM users")
    fun getRegisteredUser(): Single<UserEntity>

    @Query("DELETE FROM users")
    fun deleteAllUsers()
}