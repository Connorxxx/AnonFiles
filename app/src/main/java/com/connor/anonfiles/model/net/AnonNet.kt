package com.connor.anonfiles.model.net

import com.connor.anonfiles.model.room.FileData
import com.drake.net.Get
import com.drake.net.Post
import com.drake.net.utils.scopeNet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AnonNet {


    fun CoroutineScope.postFile(file: File) = Post<FileModel>("upload") {
        param("file", file)
    }

    fun CoroutineScope.downloadFile(url: String) = Get<File>(url)


    fun postFile(file: File, data: FileModel): FileData {
        val fileData = FileData(
            data.data.file.url.full,
            data.data.file.url.short,
            data.data.file.metadata.id,
            data.data.file.metadata.name,
            data.data.file.metadata.size.readable
        )
        if (data.status && file.exists()) file.delete()
        return fileData
    }

//    suspend fun downloadFile(url: String) = suspendCoroutine<File> {
//        scopeNet(Dispatchers.IO) {
//            val file = Get<File>(url).await()
//            it.resume(file)
//        }
//    }

}