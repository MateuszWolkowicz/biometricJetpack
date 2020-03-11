package com.wolkowiczmateusz.biometricjetpack.login.presentation

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wolkowiczmateusz.biometricjetpack.R
import com.wolkowiczmateusz.biometricjetpack.infrastructure.InternalErrorConverter
import com.wolkowiczmateusz.biometricjetpack.infrastructure.extensions.Event
import com.wolkowiczmateusz.biometricjetpack.infrastructure.extensions.isNotDisposed
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.BiometricCryptoObject
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.BiometricState
import com.wolkowiczmateusz.biometricjetpack.login.data.LoginRepository
import com.wolkowiczmateusz.biometricjetpack.login.model.LoginFormState
import com.wolkowiczmateusz.biometricjetpack.login.model.LoginResult
import com.wolkowiczmateusz.biometricjetpack.model.User
import com.wolkowiczmateusz.biometricjetpack.repos.data.GithubRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginScreenViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val githubRepository: GithubRepository,
    private val internalErrorConverter: InternalErrorConverter
): ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private var loginDisposable: Disposable? = null
    private var registeredUserDisposable: Disposable? = null

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _biometricEvent = MutableLiveData<Event<BiometricState>>()
    val biometricEvent: LiveData<Event<BiometricState>> = _biometricEvent

    private val _isBiometricStillAvailable = MutableLiveData<Event<BiometricCryptoObject>>()
    val isBiometricStillAvailable: LiveData<Event<BiometricCryptoObject>> =
        _isBiometricStillAvailable

    fun login(username: String, password: String) {
        _loginResult.value = LoginResult.Loading
        val loginState = credentialsValidation(username, password)
        if (!loginState.isDataValid) {
            _loginResult.value = LoginResult.Error(loginState)
            return
        }

        if (loginDisposable.isNotDisposed()) {
            return
        }

        loginDisposable = loginRepository.login(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user -> processSuccess(user) },
                { throwable -> processError(throwable) }
            )
        compositeDisposable.add(loginDisposable!!)
    }

    private fun processSuccess(user: User) {
        if (user.name == null) {
            _loginResult.value = LoginResult.Error(
                LoginFormState(
                    isDataValid = false,
                    passwordError = R.string.invalid_username_or_password,
                    emailError = R.string.invalid_username_or_password
                )
            )
        } else {
            _loginResult.value = LoginResult.Success(LoginFormState(isDataValid = true))
        }
    }

    private fun processError(message: Throwable) {
        val internalErrorType = internalErrorConverter.convertToGeneralErrorType(message)
        _loginResult.value = LoginResult.Error(LoginFormState(errorType = internalErrorType))
    }

    private fun credentialsValidation(
        username: String,
        password: String
    ): LoginFormState {
        val loginState = LoginFormState(isDataValid = true)

        if (!isUserNameValid(username)) {
            with(loginState) {
                emailError = R.string.invalid_username
                isDataValid = false
            }

        }
        if (!isPasswordValid(password)) {
            with(loginState) {
                passwordError = R.string.invalid_password
                isDataValid = false
            }
        }
        return loginState
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
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
                { processRegisteredUserForBiometricError() }
            )
        compositeDisposable.add(registeredUserDisposable!!)
    }

    private fun processRegisteredUserForBiometricError() {
        _biometricEvent.value = Event(BiometricState.USER_NOT_REGISTERED)
        _loginResult.value = LoginResult.Error(LoginFormState())
    }

    private fun processRegisteredUserForBiometricLoginSuccess(user: User) {
        if (user.name == null) {
            _biometricEvent.value = Event(BiometricState.USER_NOT_REGISTERED)
        } else {
            loginRepository.createBiometricKey()
            _biometricEvent.value = Event(BiometricState.USER_REGISTERED)
        }
    }

    fun getRegisteredUser() {

        if (registeredUserDisposable.isNotDisposed()) {
            return
        }

        registeredUserDisposable = loginRepository.getRegisteredUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user -> processRegisteredUserSuccess(user) },
                { processRegisteredUserError() }
            )
        compositeDisposable.add(registeredUserDisposable!!)
    }

    private fun processRegisteredUserError() {
        _loginResult.value = LoginResult.Error(LoginFormState())
    }

    private fun processRegisteredUserSuccess(user: User) {
        if (user.name == null) {
            _loginResult.value = LoginResult.Error(LoginFormState(isDataValid = false))
        } else {
            _loginResult.value =
                LoginResult.Success(LoginFormState(isDataValid = false, userEmail = user.name))
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
    }
}