package com.wolkowiczmateusz.biometricjetpack.repos.data.api

import com.squareup.moshi.Json

data class GithubReposResponse(
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "description")
    val description: String?,
    @field:Json(name = "stargazers_count")
    val stars: Long
)