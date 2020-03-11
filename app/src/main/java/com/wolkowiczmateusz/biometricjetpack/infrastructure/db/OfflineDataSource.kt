package com.wolkowiczmateusz.biometricjetpack.infrastructure.db

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.paging.DataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.ReposDao
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.UsersDao
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.model.UserEntity
import io.reactivex.Single
import javax.inject.Inject

class OfflineDataSource @Inject constructor(
    private val usersDao: UsersDao,
    private val sharedPreferences: SharedPreferences,
    private val reposDao: ReposDao
) {
    fun deleteAllUsers() {
        usersDao.deleteAllUsers()
    }

    fun getRegisteredUser(): Single<UserEntity> {
        return usersDao.getRegisteredUser()
    }

    fun getUserByEmail(email: String): Single<List<UserEntity>> {
        return usersDao.getUserByEmail(email)
    }

    fun insertUser(userEntity: UserEntity) {
        usersDao.insert(userEntity)
    }

    fun loginUserState(logIn: Boolean) {
        sharedPreferences.edit {
            putBoolean(USER_LOG_IN, logIn)
        }
    }

    fun isUserLogIn(): Boolean {
        return sharedPreferences.getBoolean(USER_LOG_IN, false)
    }

    fun getAllRepositoriesByStars(): Single<List<GithubRepoEntity>> {
        return reposDao.getAllRepositoriesByStars()
    }

    fun getAllRepositoriesByStarsAsDataSourceOffline(): DataSource.Factory<Int, GithubRepoEntity> {
        return reposDao.getAllRepositoriesByStarsAsDataSourceOffline()
    }

    fun getAllRepositoriesById(githubRepoId: String): Single<GithubRepoEntity> {
        return reposDao.getAllRepositoriesById(githubRepoId)
    }

    fun deleteAllReposData() {
        reposDao.deleteAllReposData()
    }

    fun insertRepos(onlineRepos: List<GithubRepoEntity>) {
        return reposDao.insert(onlineRepos)
    }

    companion object {
        private const val USER_LOG_IN = "USER_LOG_IN"
    }
}
