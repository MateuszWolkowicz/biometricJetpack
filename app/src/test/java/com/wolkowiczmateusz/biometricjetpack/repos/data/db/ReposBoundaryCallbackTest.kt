package com.wolkowiczmateusz.biometricjetpack.repos.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.wolkowiczmateusz.biometricjetpack.infrastructure.InternalErrorConverter
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.OfflineDataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.OnlineDataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.ReposDao
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.UsersDao
import com.wolkowiczmateusz.biometricjetpack.repos.data.GithubRepositoryImpl
import com.wolkowiczmateusz.biometricjetpack.repos.data.api.GithubApi
import com.wolkowiczmateusz.biometricjetpack.repos.data.api.GithubReposResponse
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.yameo.merchant.AppDatabase

@RunWith(AndroidJUnit4::class)
class ReposBoundaryCallbackTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var usersDao: UsersDao
    private lateinit var reposDao: ReposDao
    private lateinit var db: AppDatabase

    private lateinit var githubRepository: GithubRepositoryImpl
    lateinit var offlineDataSource: OfflineDataSource
    private lateinit var onlineDataSource: OnlineDataSource
    lateinit var githubApi: GithubApi
    lateinit var internalErrorConverter: InternalErrorConverter

    private lateinit var reposBoundaryCallback: ReposBoundaryCallback

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        usersDao = db.usersDao()
        reposDao = db.reposDao()
        githubApi = mockk()
        internalErrorConverter = mockk()
        offlineDataSource = OfflineDataSource(usersDao, mockk(), reposDao)
        onlineDataSource = OnlineDataSource(githubApi)
        githubRepository = GithubRepositoryImpl(offlineDataSource, onlineDataSource)
        reposBoundaryCallback = ReposBoundaryCallback(githubRepository, internalErrorConverter)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun tearDown() {
        db.close()
        RxJavaPlugins.reset()
    }

    @Test
    fun `retrieved data from online api should be properly saved offline`() {
        val newListOfRepos = mutableListOf(
            GithubReposResponse(
                id = 1,
                name = "first",
                description = "first description",
                stars = 10
            ),
            GithubReposResponse(
                id = 2,
                name = "second",
                description = "second description",
                stars = 5
            )
        )

        val newListOfReposEntity = mutableListOf(
            GithubRepoEntity(
                repoId = 1,
                name = "first",
                description = "first description",
                stars = 10
            ),
            GithubRepoEntity(
                repoId = 2,
                name = "second",
                description = "second description",
                stars = 5
            )
        )
        every { githubRepository.getRepositoriesOnline(any(), any()) } returns Single.just(
            newListOfRepos
        )
        reposBoundaryCallback.onZeroItemsLoaded()
        val testObserver = reposDao.getAllRepositoriesByStars().test().await()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        val values = testObserver.values()[0]
        testObserver.assertValues(newListOfReposEntity)
        Truth.assertThat(values).hasSize(2)
        Truth.assertThat(values).containsExactlyElementsIn(
            newListOfReposEntity
        )
    }
}