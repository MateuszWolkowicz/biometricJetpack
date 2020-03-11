package com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity
import io.reactivex.Single

@Dao
interface ReposDao {

    @Query("SELECT * FROM repos ORDER BY stars DESC, name ASC")
    fun getAllRepositoriesByStarsAsDataSourceOffline(): DataSource.Factory<Int, GithubRepoEntity>

    @Query("SELECT * FROM repos ORDER BY stars DESC, name ASC")
    fun getAllRepositoriesByStars(): Single<List<GithubRepoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<GithubRepoEntity>)

    @Query("SELECT * FROM repos where repo_id = :githubRepoId")
    fun getAllRepositoriesById(githubRepoId: String): Single<GithubRepoEntity>

    @Query("DELETE FROM repos")
    fun deleteAllReposData()
}