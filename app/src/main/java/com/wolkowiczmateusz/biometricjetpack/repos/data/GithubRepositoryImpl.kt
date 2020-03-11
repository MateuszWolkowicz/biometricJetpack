package com.wolkowiczmateusz.biometricjetpack.repos.data

import androidx.paging.DataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.OfflineDataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.OnlineDataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity
import com.wolkowiczmateusz.biometricjetpack.repos.data.api.GithubReposResponse
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val offlineDataSource: OfflineDataSource,
    private val onlineDataSource: OnlineDataSource
): GithubRepository {

    override fun getRepositoriesOnline(
        page: Int,
        requestedLoadSize: Int
    ): Single<List<GithubReposResponse>> {
        return onlineDataSource.loadGitHubRepositoriesByStart(page, requestedLoadSize)
            .map { it.items }
    }

    override fun getAllRepositoriesByStarsAsDataSourceOffline(): DataSource.Factory<Int, GithubRepoEntity>  {
        return offlineDataSource.getAllRepositoriesByStarsAsDataSourceOffline()
    }

    override fun getAllRepositoriesByStarsOffline(): Single<List<GithubRepoEntity>> {
        return offlineDataSource.getAllRepositoriesByStars()
    }

    override fun saveOffline(onlineRepos: List<GithubRepoEntity>): Completable {
        return Completable.fromCallable { offlineDataSource.insertRepos(onlineRepos) }
    }

    override fun getGithubRepositoryByIdOffline(githubRepoId: String): Single<GithubRepoEntity> {
        return offlineDataSource.getAllRepositoriesById(githubRepoId)
    }

    override fun clearAllData() {
        offlineDataSource.deleteAllReposData()
    }

}