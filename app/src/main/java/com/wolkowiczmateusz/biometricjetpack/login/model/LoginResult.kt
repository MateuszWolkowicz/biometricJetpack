package com.wolkowiczmateusz.biometricjetpack.login.model

sealed class LoginResult {
    object Loading : LoginResult()
    data class Error(val loginFormState: LoginFormState?) : LoginResult()
    data class Success(val loginFormState: LoginFormState?) : LoginResult()
}