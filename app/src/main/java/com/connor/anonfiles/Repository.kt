package com.connor.anonfiles

import com.connor.anonfiles.model.net.AnonNet
import com.connor.anonfiles.model.room.FileDao
import com.connor.anonfiles.model.room.FileData
import kotlinx.coroutines.flow.flatMapMerge
import java.io.File

class Repository(private val fileDao: FileDao, private val anonNet: AnonNet) {

    fun getFileDatabase() = fileDao.loadAllFile()

    fun getFileDatabaseByName() = fileDao.loadAllFileByName()

    fun getFileDatabaseBySize() = fileDao.loadAllFileBySize()

    fun getFileDatabaseByQueryName(searchName: String) = fileDao.queryFileName(searchName)

    suspend fun deleteFileDatabase(fileId: String) {
            fileDao.deleteFile(fileId)
    }

    suspend fun postFile(file: File): FileData {
        val fileData = anonNet.postFile(file)
        fileData.id = fileDao.insertFile(fileData)
        return fileData
    }
}