package com.connor.anonfiles.viewmodel

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.*
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.fullName
import com.connor.anonfiles.App.Companion.context
import com.connor.anonfiles.Repository
import com.connor.anonfiles.model.room.FileData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import java.io.File

class MainViewModel(private val repository: Repository) : ViewModel() {

    private lateinit var storageHelper: SimpleStorageHelper

    private val fileLiveData = MutableLiveData<File>()

    private val dlFileLiveData = MutableLiveData<String>()

    val fileData: LiveData<FileData> = Transformations.switchMap(fileLiveData) {
        repository.postFile(it)
    }

    val dlFileData: LiveData<File> = Transformations.switchMap(dlFileLiveData) {
        repository.downloadFile(it)
    }

    private fun getFileData(file: File) {
        fileLiveData.postValue(file)
    }

    fun downloadFile(url: String) {
        dlFileLiveData.value = url
    }

    fun getFileDatabase() {
        repository.getFileDatabase()
    }

    fun deleteFileDatabase(fileId: String) {
        repository.deleteFileDatabase(fileId)
    }

    fun getFileList() = repository.getFileList()

    fun openFilePicker() {
        storageHelper.openFilePicker()
    }

    fun setupSimpleStorage(activity: ComponentActivity) {
        storageHelper = SimpleStorageHelper(activity)
        storageHelper.onFileSelected = { _, files ->
            val documentFile = files.first()
            viewModelScope.launch(Dispatchers.IO) {
                val file = getFile(documentFile.uri, documentFile.fullName)
                getFileData(file)
            }

        }
    }

    private suspend fun getFile(
        uri: Uri,
        name: String,
    ) = withContext(Dispatchers.IO) {
        val file = File(context.filesDir.path, name)
        kotlin.runCatching {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.source()?.buffer().use { buffer ->
                buffer?.readAll(file.sink())
            }
           // it.resume(file)
        }
        file
    }

//    private suspend fun getFile(uri: Uri, name: String): File {
//        val file = File(context.filesDir.path, name)
//        withContext(Dispatchers.IO) {
//            kotlin.runCatching {
//                val inputStream = context.contentResolver.openInputStream(uri)
//                inputStream?.source()?.buffer().use { buffer ->
//                    buffer?.readAll(file.sink())
//                }
//            }
//        }
//        return file
//    }

    override fun onCleared() {
        super.onCleared()
        repository.job.cancel()
    }
}