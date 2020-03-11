package com.wolkowiczmateusz.biometricjetpack.infrastructure.di.core

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.ReposDao
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.UsersDao
import dagger.BindsInstance
import dagger.Component
import pl.yameo.merchant.AppDatabaseModule
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainAppModule::class,
        NetworkModule::class,
        AppDatabaseModule::class
    ]
)
interface CoreComponent {

    fun sharedPref(): SharedPreferences
    fun resources(): Resources
    fun retrofit(): Retrofit
    fun userDao(): UsersDao
    fun reposDao(): ReposDao

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): CoreComponent
    }

}