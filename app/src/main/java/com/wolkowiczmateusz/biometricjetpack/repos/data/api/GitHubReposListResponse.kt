package com.wolkowiczmateusz.biometricjetpack.repos.data.api

import com.squareup.moshi.Json

data class GitHubReposListResponse(
    @field:Json(name = "total_count")
    val totalCount: Int,
    @field:Json(name = "incomplete_results")
    val incompleteResults: Boolean,
    @field:Json(name = "items")
    val items: List<GithubReposResponse>
)