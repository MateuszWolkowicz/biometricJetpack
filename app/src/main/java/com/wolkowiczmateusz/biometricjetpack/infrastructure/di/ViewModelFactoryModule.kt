package pl.yameo.merchant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wolkowiczmateusz.biometricjetpack.infrastructure.di.DaggerViewModelFactory
import com.wolkowiczmateusz.biometricjetpack.login.presentation.LoginScreenViewModel
import com.wolkowiczmateusz.biometricjetpack.mainview.MainViewModel
import com.wolkowiczmateusz.biometricjetpack.repo.GithubRepoViewModel
import com.wolkowiczmateusz.biometricjetpack.repos.presentation.GithubReposViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import org.lmms.lora.ViewModelKey

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginScreenViewModel::class)
    abstract fun bindLoginScreenViewModel(loginScreenViewModel: LoginScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GithubReposViewModel::class)
    abstract fun bindGithubReposViewModel(githubReposViewModel: GithubReposViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GithubRepoViewModel::class)
    abstract fun bindGithubRepoViewModel(githubRepoViewModel: GithubRepoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

}