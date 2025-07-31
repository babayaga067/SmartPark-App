package com.example.smartpark.utils

/**
 * Standardized UI state wrapper for Compose/MVVM UI layers.
 */
sealed class UiState<out T> {
    /** Initial state, nothing happening */
    object Idle : UiState<Nothing>()

    /** In-progress state, e.g., loading spinner */
    object Loading : UiState<Nothing>()

    /** Success state, holds the data to be displayed */
    data class Success<T>(val data: T) : UiState<T>()

    /** Error state, with optional throwable cause and fallback message */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : UiState<Nothing>()

    /** Explicit blank/empty data state, useful for lists or searches */
    object Empty : UiState<Nothing>()
}
