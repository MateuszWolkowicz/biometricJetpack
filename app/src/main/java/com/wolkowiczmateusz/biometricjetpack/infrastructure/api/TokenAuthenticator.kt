package com.wolkowiczmateusz.biometricjetpack.infrastructure.api

import android.annotation.SuppressLint
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException
import javax.inject.Inject

class TokenAuthenticator @Inject constructor() : Authenticator {

    @SuppressLint("CheckResult")
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        return if (401 == response.code()) {

//TODO: Logic for refresh token should to go here

            val token = "fdsf"
            return response.request().newBuilder()
                .header(Constants.httpHeaderAuthorization, token)
                .build()
        } else {
            null
        }
    }
}