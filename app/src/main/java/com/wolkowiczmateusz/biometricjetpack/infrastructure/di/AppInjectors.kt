package com.wolkowiczmateusz.biometricjetpack.infrastructure.di

import com.wolkowiczmateusz.biometricjetpack.login.presentation.LoginScreenFragment
import com.wolkowiczmateusz.biometricjetpack.mainview.MainActivity
import com.wolkowiczmateusz.biometricjetpack.repo.GithubRepoFragment
import com.wolkowiczmateusz.biometricjetpack.repos.presentation.GithubReposFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppInjectors {

    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun loginScreenFragment(): LoginScreenFragment

    @ContributesAndroidInjector
    abstract fun githubReposFragment(): GithubReposFragment

    @ContributesAndroidInjector
    abstract fun githubRepoFragment(): GithubRepoFragment

}