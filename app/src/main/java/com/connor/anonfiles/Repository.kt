package com.connor.anonfiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.connor.anonfiles.model.net.AnonNet
import com.connor.anonfiles.model.room.FileDao
import com.connor.anonfiles.model.room.FileData
import com.connor.anonfiles.tools.showToast
import com.drake.brv.annotaion.ItemOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class Repository(private val fileDao: FileDao, private val anonNet: AnonNet) {

    fun getFileDatabase() = fileDao.loadAllFile()

    fun getFileDatabaseByName() = fileDao.loadAllFileByName()

    fun getFileDatabaseBySize() = fileDao.loadAllFileBySize()

    fun getFileDatabaseByQueryName(searchName: String) = liveData(Dispatchers.IO) {
        val searchList = fileDao.queryFileName(searchName)
        searchList.forEach {
            it.itemOrientationSwipe = ItemOrientation.NONE
        }
        emit(searchList)
    }

    suspend fun deleteFileDatabase(fileId: String) {
            fileDao.deleteFile(fileId)
    }

    fun postFile(file: File): LiveData<FileData> = liveData(Dispatchers.IO) {
            val fileData = anonNet.postFile(file)
            fileData.id = fileDao.insertFile(fileData)
            emit(fileData)
    }

    fun downloadFile(url: String) = liveData(Dispatchers.IO) {
        kotlin.runCatching {
            emit(anonNet.downloadFile(url).await())
        }.onFailure {
            withContext(Dispatchers.Main) {
                "The file you are looking for does not exist!".showToast()
            }
        }

    }
}