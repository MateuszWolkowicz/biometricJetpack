package com.wolkowiczmateusz.biometricjetpack.infrastructure.extensions

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun markAsHandled() {
        hasBeenHandled = true
    }
}