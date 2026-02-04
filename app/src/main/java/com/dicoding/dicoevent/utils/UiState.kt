package com.dicoding.dicoevent.utils

sealed class UiState<out T> {
    // Status saat sedang memuat
    object Loading : UiState<Nothing>()

    // Status ketika data sukses didapatkan
    data class Success<out T>(val data: T) : UiState<T>()

    // Status ketika terjadi error
    data class Error(val errorMessage: String) : UiState<Nothing>()
}