package com.arsfutura.sampleappble.impl.util

class Result<T> private constructor(private val value: T?, private val exception: Exception?) {
    companion object {
        fun <T> success(data: T): Result<T> = Result(data, null)

        fun <T> error(e: Exception): Result<T> = Result(null, e)

    }

    fun isSuccess(): Boolean = value != null

    fun isError(): Boolean = exception != null

    fun value(): T {
        if (isSuccess()) return value!!
        else throw IllegalArgumentException("Data is null!")
    }

    fun cause(): Exception {
        if (isError()) return exception!!
        else throw IllegalArgumentException("Exception is null!")
    }
}