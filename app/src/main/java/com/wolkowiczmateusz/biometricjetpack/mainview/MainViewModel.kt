package com.wolkowiczmateusz.biometricjetpack.mainview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.wolkowiczmateusz.biometricjetpack.infrastructure.extensions.Event
import com.wolkowiczmateusz.biometricjetpack.infrastructure.extensions.isNotDisposed
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.BiometricCryptoObject
import com.wolkowiczmateusz.biometricjetpack.login.data.LoginRepository
import com.wolkowiczmateusz.biometricjetpack.model.User
import com.wolkowiczmateusz.biometricjetpack.repos.data.GithubRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val githubRepository: GithubRepository
) : ViewModel() {

    private val _countdownNotification = MutableLiveData<Event<Boolean>>()
    val countdownNotification: LiveData<Event<Boolean>> = _countdownNotification

    private val compositeDisposable = CompositeDisposable()
    private var timerDisposable: Disposable? = null
    private var registeredUserDisposable: Disposable? = null
    private var connectivityDisposable: Disposable? = null

    private val _internetState = MutableLiveData<Boolean>()
    val internetState: MutableLiveData<Boolean> = _internetState

    private val _biometricEvent = MutableLiveData<Event<Boolean>>()
    val biometricEvent: LiveData<Event<Boolean>> = _biometricEvent

    private val _isBiometricStillAvailable = MutableLiveData<Event<BiometricCryptoObject>>()
    val isBiometricStillAvailable: LiveData<Event<BiometricCryptoObject>> =
        _isBiometricStillAvailable

    private val _logoutEvent = MutableLiveData<Event<Boolean>>()
    val logoutEvent: LiveData<Event<Boolean>> = _logoutEvent

    private fun changeInternetState(internetState: Boolean) {
        _internetState.value = internetState
    }

    fun startNotificationTimer() {
        if (timerDisposable.isNotDisposed()) {
            return
        }
        timerDisposable = Observable.interval(TIME_TO_TICK, TimeUnit.MINUTES)
            //TODO : Just use is for tests
//        timerDisposable = Observable.interval(30, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (isUserLogIn()) {
                        _countdownNotification.value = Event(true)
                    }
                },
                { throwable -> Timber.d("Timer Error $throwable") }
            )
        compositeDisposable.add(timerDisposable!!)
    }

    private fun isUserLogIn(): Boolean {
        return loginRepository.isUserLogIn()
    }

    override fun onCleared() {
        super.onCleared()
        stopNotificationTimer()
        compositeDisposable.clear()
    }

    fun stopNotificationTimer() {
        compositeDisposable.clear()
    }

    fun tryToUseBiometric() {
        if (registeredUserDisposable.isNotDisposed()) {
            return
        }
        registeredUserDisposable = loginRepository.getRegisteredUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user -> processRegisteredUserForBiometricLoginSuccess(user) },
                { Timber.d("Error while retrieving users") }
            )
        compositeDisposable.add(registeredUserDisposable!!)
    }

    private fun processRegisteredUserForBiometricLoginSuccess(user: User) {
        if (user.name != null) {
            createBiometricKey()
            _biometricEvent.value = Event(true)
        }
    }

    fun isNewBiometric() {
        val biometricCryptoObject = loginRepository.getBiometricPrompt()
        _isBiometricStillAvailable.value = Event(biometricCryptoObject)
    }

    fun clearAllData() {
        val disposable = Schedulers.single().scheduleDirect {
            loginRepository.clearAllData()
            githubRepository.clearAllData()
        }
        compositeDisposable.add(disposable)
        _logoutEvent.value = Event(true)
    }

    private fun createBiometricKey() {
        loginRepository.createBiometricKey()
    }

    fun logoutUser() {
        loginRepository.loginUserState(false)
    }

    fun logInUser() {
        loginRepository.loginUserState(true)
    }

    fun observeInternetState() {
        connectivityDisposable = ReactiveNetwork
            .observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ networkAvailability ->
                if (networkAvailability) {
                    changeInternetState(true)
                    Timber.d("Internet state: ON")
                } else {
                    changeInternetState(false)
                    Timber.d("Internet state: OFF")
                }
            },
                {
                    changeInternetState(false)
                    Timber.d("Internet state: ERROR")
                })

        compositeDisposable.add(connectivityDisposable!!)
    }

    companion object {
        const val TIME_TO_TICK: Long = 10
    }
}