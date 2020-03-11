package com.wolkowiczmateusz.biometricjetpack.repos.data

import androidx.paging.DataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity
import com.wolkowiczmateusz.biometricjetpack.repos.data.api.GithubReposResponse
import io.reactivex.Completable
import io.reactivex.Single

interface GithubRepository {
    fun getRepositoriesOnline(page: Int, requestedLoadSize: Int): Single<List<GithubReposResponse>>
    fun getAllRepositoriesByStarsAsDataSourceOffline(): DataSource.Factory<Int, GithubRepoEntity>
    fun getAllRepositoriesByStarsOffline(): Single<List<GithubRepoEntity>>
    fun saveOffline(onlineRepos: List<GithubRepoEntity>) : Completable
    fun getGithubRepositoryByIdOffline(githubRepoId: String): Single<GithubRepoEntity>
    fun clearAllData()
}