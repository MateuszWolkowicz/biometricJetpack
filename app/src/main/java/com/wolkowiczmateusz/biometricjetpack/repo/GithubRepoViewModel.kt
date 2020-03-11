package com.wolkowiczmateusz.biometricjetpack.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity
import com.wolkowiczmateusz.biometricjetpack.infrastructure.extensions.isNotDisposed
import com.wolkowiczmateusz.biometricjetpack.repos.data.GithubRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class GithubRepoViewModel @Inject constructor(
    private val githubRepository: GithubRepository
): ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private var repoByIdDisposable: Disposable? = null

    private val _repo = MutableLiveData<GithubRepoEntity>()
    val repo: LiveData<GithubRepoEntity> = _repo

    fun getGithubRepositoryById(githubRepoId: String) {
        if (repoByIdDisposable.isNotDisposed()) {
            return
        }
        repoByIdDisposable =
            githubRepository.getGithubRepositoryByIdOffline(githubRepoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _repo.value = it },
                    { throwable -> Timber.d("Timer Error $throwable") }
                )
        compositeDisposable.add(repoByIdDisposable!!)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
