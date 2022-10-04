package com.connor.anonfiles.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.fullName
import com.connor.anonfiles.App.Companion.context
import com.connor.anonfiles.Repository
import com.connor.anonfiles.model.room.FileData
import com.connor.anonfiles.tools.showToast
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.listener.DefaultItemTouchCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import okio.buffer
import okio.sink
import okio.source
import java.io.File

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _uploadChannel = Channel<FileData>()

    private val _queryChannel = Channel<Flow<List<FileData>>>()

    val getFileDatabase = repository.getFileDatabase() //.asLiveData(Dispatchers.IO)

    val getFileDatabaseByName = repository.getFileDatabaseByName()

    val getFileDatabaseBySize = repository.getFileDatabaseBySize()

    @OptIn(FlowPreview::class)
    val queryName = _queryChannel.receiveAsFlow()
        .flatMapMerge { it }
        .onEach {
            it.forEach { data ->
                data.itemOrientationSwipe = ItemOrientation.NONE
            }
        }.flowOn(Dispatchers.IO)

    val uploadFLow = _uploadChannel.receiveAsFlow()

    fun query(name: String) {
        viewModelScope.launch {
            _queryChannel.trySend(repository.getFileDatabaseByQueryName(name))
        }
    }

    private fun upChannel(file: File) {
        viewModelScope.launch {
            _uploadChannel.trySend(repository.postFile(file))
        }
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
                upChannel(file)
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