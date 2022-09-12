package com.connor.anonfiles.viewmodel

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.*
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.fullName
import com.connor.anonfiles.App.Companion.context
import com.connor.anonfiles.Repository
import com.connor.anonfiles.model.room.FileData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import java.io.File

class MainViewModel(private val repository: Repository) : ViewModel() {

    private lateinit var storageHelper: SimpleStorageHelper

    private val upFileLiveData = MutableLiveData<File>()

    private val dlFileLiveData = MutableLiveData<String>()

    private val fileQueryNameLiveData = MutableLiveData<String>()

    val upFileData: LiveData<FileData> = Transformations.switchMap(upFileLiveData) {
        repository.postFile(it)
    }

    val dlFileData: LiveData<File> = Transformations.switchMap(dlFileLiveData) {
        repository.downloadFile(it)
    }

    val getFileDatabase = repository.getFileDatabase().asLiveData(Dispatchers.IO)

    val getFileDatabaseByName = repository.getFileDatabaseByName().asLiveData(Dispatchers.IO)

    val getFileDatabaseBySize = repository.getFileDatabaseBySize().asLiveData(Dispatchers.IO)

    val getFileDatabaseByQueryName = Transformations.switchMap(fileQueryNameLiveData) {
        repository.getFileDatabaseByQueryName(it)
    }

    private fun getFileData(file: File) {
        upFileLiveData.postValue(file)
    }

    fun queryName(fileName: String) {
        fileQueryNameLiveData.value = fileName
    }

    fun downloadFile(url: String) {
        dlFileLiveData.value = url
    }

    fun deleteFileDatabase(fileId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFileDatabase(fileId)
        }
    }

//    fun getFileList() = repository.getFileList()

//    fun getFileDatabase() {
//        repository.getFileDatabase()
//    }

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
        }
        file
    }
}