package com.connor.anonfiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import com.connor.anonfiles.model.net.AnonNet
import com.connor.anonfiles.model.room.FileDao
import com.connor.anonfiles.model.room.FileData
import com.drake.brv.annotaion.ItemOrientation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class Repository(private val fileDao: FileDao, private val anonNet: AnonNet) {

    val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    fun getFileDatabase() = fileDao.loadAllFile()

    fun getFileDatabaseByName() = fileDao.loadAllFileByName()

    fun getFileDatabaseBySize() = fileDao.loadAllFileBySize()

    fun getFileDatabaseByQueryName(searchName: String) = liveData(Dispatchers.IO) {
        val test = fileDao.queryFileName(searchName)
        test.forEach {
            it.itemOrientationSwipe = ItemOrientation.NONE
        }
        emit(test)
    }

    fun deleteFileDatabase(fileId: String) {
        ioScope.launch {
            fileDao.deleteFile(fileId)
        }
    }

    fun postFile(file: File): LiveData<FileData> = liveData(Dispatchers.IO) {
            val fileData = anonNet.postFile(file)
            fileData.id = fileDao.insertFile(fileData)
            emit(fileData)
    }

    fun downloadFile(url: String) = liveData(Dispatchers.IO) {
            emit(anonNet.downloadFile(url).await())
    }
}