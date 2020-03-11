package com.wolkowiczmateusz.biometricjetpack.infrastructure

import com.wolkowiczmateusz.biometricjetpack.login.model.ErrorType

interface InternalErrorConverter {
 fun convertToGeneralErrorType(error: Throwable) : ErrorType
}
