package com.ara.aranote.util

/**
 * A Result implementation class.
 * It's a sealed class which supports two types: Success And Error.
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val errorMessage: String = "") : Result<Nothing>()
}
