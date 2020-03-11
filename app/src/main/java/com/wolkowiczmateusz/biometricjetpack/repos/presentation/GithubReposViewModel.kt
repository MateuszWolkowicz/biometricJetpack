package com.wolkowiczmateusz.biometricjetpack.repos.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity
import com.wolkowiczmateusz.biometricjetpack.repos.data.GithubRepository
import com.wolkowiczmateusz.biometricjetpack.repos.data.db.RepoQueryResult
import com.wolkowiczmateusz.biometricjetpack.repos.data.db.ReposBoundaryCallback
import javax.inject.Inject

class GithubReposViewModel @Inject constructor(
    private val githubRepository: GithubRepository,
    private val reposBoundaryCallback: ReposBoundaryCallback
): ViewModel() {

    private val _queryReposLiveData = MutableLiveData<Boolean>()

    private val _repoResult: LiveData<RepoQueryResult> = Transformations.map(_queryReposLiveData) {
        callPagedListQuery()
    }

    val repos: LiveData<PagedList<GithubRepoEntity>> = switchMap(_repoResult) { it.data }
    val githubError: LiveData<Boolean> = switchMap(_repoResult) { it.githubErrors }
    val networkError: LiveData<Boolean> = switchMap(_repoResult) { it.networkErrors }
    val isRequestInProgressLiveData: LiveData<Boolean> =
        switchMap(_repoResult) { it.isRequestInProgressLiveData }

    fun getReposByStars() {
        _queryReposLiveData.value = true
    }

    private fun callPagedListQuery(): RepoQueryResult {
        val dataSourceFactory = githubRepository.getAllRepositoriesByStarsAsDataSourceOffline()
        val githubErrors = reposBoundaryCallback.githubError
        val networkErrors = reposBoundaryCallback.networkError
        val isRequestInProgressLiveData = reposBoundaryCallback.isRequestInProgressLiveData
        val data = LivePagedListBuilder(
            dataSourceFactory,
            DATABASE_PAGE_SIZE
        )
            .setBoundaryCallback(reposBoundaryCallback)
            .build()
        return RepoQueryResult(
            data,
            isRequestInProgressLiveData,
            githubErrors,
            networkErrors
        )
    }

    override fun onCleared() {
        super.onCleared()
        reposBoundaryCallback.onCleared()
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }
}
