package com.connor.anonfiles.test

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

object Coroutines {
    val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    fun somethingUsefulOneAsync() = ioScope.async { doSomethingUsefulOne() }

    fun somethingUsefulTwoAsync() = ioScope.async { doSomethingUsefulTwo() }

    suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        one.await() + two.await()
    }


    private suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // 假设我们在这里做了些有用的事
        return 13
    }

    private suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // 假设我们在这里也做了些有用的事
        return 29
    }
}

fun main() {
    val time = measureTimeMillis {
        val two = Coroutines.somethingUsefulTwoAsync()
        val one = Coroutines.somethingUsefulOneAsync()

            runBlocking {
                println("The sum is ${Coroutines.concurrentSum()}")
                println("The answer is ${one.await() + two.await()}")
            }

    }
    println("Completed in $time ms")
}