package com.connor.anonfiles.model.net

import com.connor.anonfiles.model.room.FileData
import com.drake.brv.annotaion.ItemOrientation
import com.drake.net.Get
import com.drake.net.Post
import kotlinx.coroutines.coroutineScope
import java.io.File

class AnonNet {

    suspend fun downloadFile(url: String) = coroutineScope {
        Get<File>(url)
    }



    suspend fun postFile(file: File): FileData {
        val data: FileModel = post(file).await()
        val fileData = FileData(
            ItemOrientation.LEFT,
            data.data.file.url.full,
            data.data.file.url.short,
            data.data.file.metadata.id,
            data.data.file.metadata.name,
            data.data.file.metadata.size.readable
        )
        if (data.status && file.exists()) file.delete()
        return fileData
    }

    private suspend fun post(file: File) = coroutineScope {
        Post<FileModel>("upload") {
            param("file", file)
        }
    }
}