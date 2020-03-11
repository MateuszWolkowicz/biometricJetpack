package com.wolkowiczmateusz.biometricjetpack.infrastructure.db

import com.wolkowiczmateusz.biometricjetpack.repos.data.api.GitHubReposListResponse
import com.wolkowiczmateusz.biometricjetpack.repos.data.api.GithubApi
import io.reactivex.Single
import javax.inject.Inject

class OnlineDataSource @Inject constructor(
    private val githubApi: GithubApi
) {
    fun loadGitHubRepositoriesByStart(page: Int, requestedLoadSize: Int): Single<GitHubReposListResponse> {
        return githubApi.loadGitHubRepositoriesByStart(page,requestedLoadSize)
    }
}
