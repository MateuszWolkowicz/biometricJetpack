package com.wolkowiczmateusz.biometricjetpack.infrastructure.di

import android.content.Context
import com.wolkowiczmateusz.biometricjetpack.App
import com.wolkowiczmateusz.biometricjetpack.infrastructure.di.core.CoreComponent
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import pl.yameo.merchant.ViewModelFactoryModule
import javax.inject.Scope

@AppScope
@Component(
    dependencies = [
        CoreComponent::class
    ],
    modules = [
        AppModule::class,
        AndroidSupportInjectionModule::class,
        AppInjectors::class,
        ViewModelFactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    override fun inject(app: App)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun coreComponent(coreComponent: CoreComponent): Builder

        fun build(): AppComponent
    }
}

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class AppScope