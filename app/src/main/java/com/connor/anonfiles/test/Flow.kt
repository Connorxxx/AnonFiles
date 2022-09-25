package com.connor.anonfiles.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun simple(): Flow<Int> = flow { // 流构建器
    for (i in 1..3) {
        delay(1000) // 假装我们在这里做了一些有用的事情
        emit(i) // 发送下一个值
    }
}

suspend fun performRequest(request: Int): String {
    delay(1000) // 模仿长时间运行的异步工作
    return "response $request"
}

fun numbers(): Flow<Int> = flow {
    try {
        (1..6).forEach {
            emit(it)
        }
    } finally {
        println("Finally in numbers")
    }
}


fun main() = runBlocking {
    simple().collect() { println(it) }

//    (1..3).asFlow().collect { println(it) }

//    (1..3).asFlow()
//        .map { performRequest(it) }
//        .collect { println(it) }

//    (1..3).asFlow()
//        .transform { request ->
//            emit("Making request $request")
//            emit(performRequest(request))
//        }.collect() { println(it) }

//    numbers()
//        .take(2)
//        .collect() { println(it) }
}
