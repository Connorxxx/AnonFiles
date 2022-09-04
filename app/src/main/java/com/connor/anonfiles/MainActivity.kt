package com.connor.anonfiles

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.connor.anonfiles.databinding.ActivityMainBinding
import com.connor.anonfiles.model.room.FileData
import com.connor.anonfiles.viewmodel.MainViewModel
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineActivity
import com.drake.logcat.LogCat
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : EngineActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModel()

    override fun initView() {
        val tag = LogCat.tag
        binding.v = this
        binding.rv.setup {
            addType<FileData>(R.layout.item_file_list)
            itemTouchHelper = ItemTouchHelper(object : DefaultItemTouchCallback() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    val fileId = (viewHolder as BindingAdapter.BindingViewHolder).getModel<FileData>().fileID
                    viewModel.deleteFileDatabase(fileId!!)
                }
            })
            R.id.card_fiie_list.onClick {
                Log.d(tag, "onSwiped: ${viewModel.getFileList()[bindingAdapterPosition].fileID}")
            }
        }
        binding.rv.models = viewModel.getFileList()
        viewModel.fileData.observe(this, Observer {
            binding.rv.addModels(listOf(viewModel.fileData.value))
            binding.rv.scrollToPosition(binding.rv.adapter!!.itemCount - 1)
        })
    }

    override fun initData() {
        viewModel.getFileDatabase()
        viewModel.setupSimpleStorage(this)
    }

    override fun onClick(v: View) {
        when (v) {
            binding.fabUpload -> {
                viewModel.openFilePicker()
            }
        }
    }
}