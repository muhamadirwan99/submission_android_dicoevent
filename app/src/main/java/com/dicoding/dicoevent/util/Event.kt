package com.dicoding.dicoevent.util


open class Event<out T>(private val content: T) {
    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set


    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            // Event sudah di-handle sebelumnya, return null agar tidak re-trigger
            null
        } else {
            // Event belum di-handle, tandai sebagai handled dan return content
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}