package com.wolkowiczmateusz.biometricjetpack

import androidx.appcompat.app.AppCompatDelegate
import com.wolkowiczmateusz.biometricjetpack.infrastructure.di.DaggerAppComponent
import com.wolkowiczmateusz.biometricjetpack.infrastructure.di.core.DaggerCoreComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

open class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val coreComponent = DaggerCoreComponent.builder()
            .context(this)
            .build()

        val appComponent = DaggerAppComponent.builder()
            .context(this)
            .coreComponent(coreComponent)
            .build()

        appComponent.inject(this)

        return appComponent
    }
}