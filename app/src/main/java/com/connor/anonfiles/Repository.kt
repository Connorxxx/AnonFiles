package com.connor.anonfiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.anggrayudi.storage.extension.postToUi
import com.connor.anonfiles.model.net.AnonNet
import com.connor.anonfiles.model.net.AnonNet.downloadFile
import com.connor.anonfiles.model.net.AnonNet.postFile
import com.connor.anonfiles.model.room.FileDao
import com.connor.anonfiles.model.room.FileData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class Repository(private val fileDao: FileDao, private val anonNet: AnonNet) {

    val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    private val fileDataList = ArrayList<FileData>()

    fun getFileList() = fileDataList

    fun getFileDatabase() {
        ioScope.launch {
            fileDataList.clear()
            (fileDao.loadAllFile()).forEach {
                fileDataList.addAll(listOf(it))
            }
        }
    }

    fun deleteFileDatabase(fileId: String) {
        ioScope.launch {
            fileDao.deleteFile(fileId)
        }
    }

    suspend fun restId() {
        fileDao.vacuumDb(SimpleSQLiteQuery("VACUUM"))
    }

    suspend fun delete(fileData: FileData) = fileDao.delete(fileData)

    fun postFile(file: File): LiveData<FileData> {
        val fileLiveData = MutableLiveData<FileData>()
        ioScope.launch {
            ioScope.postToUi {  }
            val fileData = postFile(file, ioScope.postFile(file).await())
            fileData.id = fileDao.insertFile(fileData)
            fileLiveData.postValue(fileData)
        }
        return fileLiveData
    }

    fun downloadFile(url: String): LiveData<File> {
        val dlLiveData = MutableLiveData<File>()
        ioScope.launch {
            dlLiveData.postValue(ioScope.downloadFile(url).await())
        }
        return dlLiveData
    }
}