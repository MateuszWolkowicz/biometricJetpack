package com.wolkowiczmateusz.biometricjetpack.repos.data.db

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity

data class RepoQueryResult(
    val data: LiveData<PagedList<GithubRepoEntity>>,
    val isRequestInProgressLiveData: LiveData<Boolean>,
    val githubErrors: LiveData<Boolean>,
    val networkErrors: LiveData<Boolean>
)
