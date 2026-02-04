package com.dicoding.dicoevent.utils

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {
        is UnknownHostException -> "No internet connection. Please check your connection"
        is SocketTimeoutException -> "Connection timeout. The server is taking too long to respond."
        is HttpException -> {
            when (this.code()) {
                404 -> "The requested data could not be found."
                500 -> "A server error occurred. Please try again later."
                else -> "Network error (${this.code()}). Please try again."
            }
        }
        else -> this.message ?: "An unknown error occurred."
    }
}