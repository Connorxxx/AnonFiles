package com.connor.anonfiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.connor.anonfiles.model.FileModel
import com.connor.anonfiles.model.room.FileDao
import com.connor.anonfiles.model.room.FileData
import com.drake.net.Post
import com.drake.net.utils.scopeNet
import com.drake.net.utils.withMain
import kotlinx.coroutines.Dispatchers
import java.io.File

class Repository(private val fileDao: FileDao) {

    private val fileDataList = ArrayList<FileData>()

    fun getFileList() = fileDataList

    suspend fun getFileDatabase() {
        fileDataList.clear()
        (fileDao.loadAllFile()).forEach {
            fileDataList.addAll(listOf(it))
        }
    }

    suspend fun deleteFileDatabase(fileId: String) {
        fileDao.deleteFile(fileId)
    }

    suspend fun restId() {
        fileDao.vacuumDb(SimpleSQLiteQuery("VACUUM"))
    }

    suspend fun delete(fileData: FileData) = fileDao.delete(fileData)

    fun postFile(file: File): LiveData<FileData> {
        val fileLiveData = MutableLiveData<FileData>()
        scopeNet(dispatcher = Dispatchers.IO) {
            val data = Post<FileModel>("upload") {
                param("file", file)
            }.await()
            val fileData = FileData(
                data.data.file.url.full,
                data.data.file.url.short,
                data.data.file.metadata.id,
                data.data.file.metadata.name,
                data.data.file.metadata.size.readable
            )
            fileData.id = fileDao.insertFile(fileData)
            withMain { fileLiveData.value = fileData }
            if (data.status && file.exists()) file.delete()
        }
        return fileLiveData
    }


}