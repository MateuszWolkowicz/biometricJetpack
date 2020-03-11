package com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class GithubRepoEntity(

    @PrimaryKey
    @ColumnInfo(name = "repo_id")
    val repoId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "stars")
    val stars: Long
)
