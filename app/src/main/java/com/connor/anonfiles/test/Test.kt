package com.connor.anonfiles.test

import com.connor.anonfiles.model.room.FileData
import com.drake.channel.receiveEventHandler

object Test {

    fun test(block: (FileData) -> Unit) {
        receiveEventHandler<FileData>("tag") {
            block(it)
        }
    }

}