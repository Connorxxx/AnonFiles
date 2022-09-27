package com.connor.anonfiles.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.fullName
import com.connor.anonfiles.App.Companion.context
import com.connor.anonfiles.Repository
import com.connor.anonfiles.tools.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import java.io.File

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val upFileLiveData = MutableLiveData<File>()

    private val dlFileLiveData = MutableLiveData<String>()

    private val fileQueryNameLiveData = MutableLiveData<String>()

    private val dlStatusFlow = MutableStateFlow("")

    val upFileData = upFileLiveData.switchMap {
        liveData(Dispatchers.IO) { emit(repository.postFile(it)) }
    }

    val dlLiveData = dlFileLiveData.switchMap {
        liveData(Dispatchers.IO) {
            kotlin.runCatching { emit(repository.downloadFile(it)) }.onFailure {
                withContext(Dispatchers.Main) {
                    "The file you are looking for does not exist!".showToast()
                }
            }

        }
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
        dlStatusFlow.value = url
    }

    fun deleteFileDatabase(fileId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFileDatabase(fileId)
        }
    }

    fun setupSimpleStorage(storageHelper: SimpleStorageHelper) {
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
    ) = coroutineScope {
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