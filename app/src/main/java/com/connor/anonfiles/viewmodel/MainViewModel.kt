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
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel(private val repository: Repository) : ViewModel() {

    private lateinit var storageHelper: SimpleStorageHelper

    private val fileLiveData = MutableLiveData<File>()

    val fileData: LiveData<FileData> = Transformations.switchMap(fileLiveData) {
        repository.postFile(it)
    }

    private fun getFileData(file: File) {
        fileLiveData.value = file
    }

    fun getFileDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFileDatabase()
        }
    }

    fun deleteFileDatabase(fileId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFileDatabase(fileId)
        }
    }

    fun deleteFile(fileData: FileData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(fileData)
        }
    }


    fun getFileList() = repository.getFileList()

    fun openFilePicker() {
        storageHelper.openFilePicker()
    }

    fun setupSimpleStorage(activity: ComponentActivity) {
        storageHelper = SimpleStorageHelper(activity)
        storageHelper.onFileSelected = { _, files ->
            val documentFile = files.first()
            viewModelScope.launch(Dispatchers.Main) {
                val file = getFile(
                    documentFile.uri,
                    documentFile.fullName
                )
                getFileData(file)
            }
        }
    }

    private suspend fun getFile(
        uri: Uri,
        name: String,
    ) = suspendCoroutine {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(context.filesDir.path, name)
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.source()?.buffer().use { buffer ->
                buffer?.readAll(file.sink())
            }
            it.resume(file)
        }
    }
}