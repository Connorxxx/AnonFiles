package com.connor.anonfiles.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.fullName
import com.connor.anonfiles.App
import com.connor.anonfiles.App.Companion.context
import com.connor.anonfiles.Repository
import com.connor.anonfiles.model.room.FileData
import com.connor.anonfiles.tools.NetworkTools.toRequestBody
import com.connor.anonfiles.tools.showToast
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.net.utils.toRequestBody
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okio.buffer
import okio.sink
import okio.source
import java.io.File

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val upFileLiveData = MutableLiveData<File>()

    private val dlFileLiveData = MutableLiveData<String>()

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

    private fun upFlow(file: File) = flow {
        emit(file)
    }.map {
        repository.postFile(file)
    }.flowOn(Dispatchers.IO)

    fun dlFlow(url: String) = flow {
        emit(url)
    }.map {
        repository.downloadFile(it)
    }.flowOn(Dispatchers.IO)
        .catch {
            "The file you are looking for does not exist!".showToast()
        }

    inline fun dlFile(url: String, crossinline block: () -> Unit) {
        viewModelScope.launch {
            dlFlow(url).collect {
                block()
            }
        }
    }

    val getFileDatabase = repository.getFileDatabase() //.asLiveData(Dispatchers.IO)

    val getFileDatabaseByName = repository.getFileDatabaseByName()

    val getFileDatabaseBySize = repository.getFileDatabaseBySize()

    @OptIn(FlowPreview::class)
    fun getFileDatabaseByQueryName(name: String) = flow {
        emit(name)
    }.flatMapConcat {
        repository.getFileDatabaseByQueryName(it)
    }.flowOn(Dispatchers.IO)



    private fun getFileData(file: File) {
        upFileLiveData.postValue(file)
    }

    fun downloadFile(url: String) {
        dlFileLiveData.value = url
    }

    fun deleteFileDatabase(fileId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFileDatabase(fileId)
        }
    }

    fun setupSimpleStorage(storageHelper: SimpleStorageHelper, block: () -> Unit) {
        storageHelper.onFileSelected = { _, files ->
            val documentFile = files.first()
            viewModelScope.launch(Dispatchers.IO) {
                val file = getFile(documentFile.uri, documentFile.fullName)
                upFlow(file).collect {
                    withContext(Dispatchers.Main) {
                        block()
                    }
                }
            }
        }
    }

    fun swiped() = ItemTouchHelper(object : DefaultItemTouchCallback() {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //super.onSwiped(viewHolder, direction)
            val adapter = viewHolder.bindingAdapter as? BindingAdapter
            val layoutPosition = viewHolder.layoutPosition
            adapter?.notifyItemRemoved(layoutPosition)
            (adapter?.models as ArrayList).removeAt(layoutPosition - 1)
            val fileId = (viewHolder as BindingAdapter.BindingViewHolder)
                .getModel<FileData>().fileID
            deleteFileDatabase(fileId!!)
        }
    })

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