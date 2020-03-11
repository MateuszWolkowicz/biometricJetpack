package com.wolkowiczmateusz.biometricjetpack.login.model

data class LoginFormState(
    var emailError: Int? = null,
    var userEmail: String? = null,
    var passwordError: Int? = null,
    var isDataValid: Boolean = false,
    var errorType: ErrorType? = null,
    var newBiometric: Boolean? = null
)