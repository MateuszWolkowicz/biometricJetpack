package com.wolkowiczmateusz.biometricjetpack.infrastructure.di

import android.content.Context
import com.wolkowiczmateusz.biometricjetpack.infrastructure.InternalErrorConverter
import com.wolkowiczmateusz.biometricjetpack.infrastructure.InternalErrorConverterImpl
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.OfflineDataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.OnlineDataSource
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.EncryptionServices
import com.wolkowiczmateusz.biometricjetpack.login.data.LoginRepository
import com.wolkowiczmateusz.biometricjetpack.login.data.LoginRepositoryImpl
import com.wolkowiczmateusz.biometricjetpack.notification.SimpleNotificationManager
import com.wolkowiczmateusz.biometricjetpack.notification.SimpleNotificationManagerImpl
import com.wolkowiczmateusz.biometricjetpack.repos.data.GithubRepository
import com.wolkowiczmateusz.biometricjetpack.repos.data.GithubRepositoryImpl
import com.wolkowiczmateusz.biometricjetpack.repos.data.api.GithubApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AppModule {

    @Provides
    fun provideRepository(
        offlineDataSource: OfflineDataSource,
        encryptionServices: EncryptionServices
    ): LoginRepository {
        return LoginRepositoryImpl(
            offlineDataSource,
            encryptionServices
        )
    }

    @Provides
    fun provideGithubRepository(offlineDataSource: OfflineDataSource, onlineDataSource: OnlineDataSource): GithubRepository {
        return GithubRepositoryImpl(
            offlineDataSource,
            onlineDataSource
        )
    }

    @Provides
    fun providesServerErrorConverter(): InternalErrorConverter {
        return InternalErrorConverterImpl()
    }

    @Provides
    fun providesEncryptionServices(context: Context): EncryptionServices {
        return EncryptionServices(context)
    }

    @Provides
    fun providesSimpleNotificationManager(): SimpleNotificationManager {
        return SimpleNotificationManagerImpl()
    }

    @AppScope
    @Provides
    fun provideGitHubApi(
        retrofit: Retrofit
    ): GithubApi {
        return retrofit.create(GithubApi::class.java)
    }

}