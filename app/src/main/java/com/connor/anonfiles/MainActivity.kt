package com.connor.anonfiles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.connor.anonfiles.databinding.ActivityMainBinding
import com.connor.anonfiles.databinding.DialogDetailsBinding
import com.connor.anonfiles.model.room.FileData
import com.connor.anonfiles.tools.VTools
import com.connor.anonfiles.viewmodel.MainViewModel
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : EngineActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModel()

    private val tools: VTools by inject()

    override fun initView() {
        binding.v = this
        setSupportActionBar(binding.toolbarMain)
        binding.rv.setup {
            addType<FileData>(R.layout.item_file_list)
            itemTouchHelper = ItemTouchHelper(object : DefaultItemTouchCallback() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    val fileId = (viewHolder as BindingAdapter.BindingViewHolder)
                        .getModel<FileData>().fileID
                    viewModel.deleteFileDatabase(fileId!!)
                }
            })
            R.id.card_fiie_list.onClick {
                val alertDialog = MaterialAlertDialogBuilder(this@MainActivity).apply {
                }
                val binding = DataBindingUtil.inflate<DialogDetailsBinding>(
                    layoutInflater,
                    R.layout.dialog_details,
                    null,
                    false
                )
                binding.m = getModel()
                alertDialog.setView(binding.root)
                alertDialog.show()
            }
            R.id.img_share.onClick {
                tools.shareLink(getModel<FileData>().shortUrl!!, this@MainActivity, binding.rv)
            }
            R.id.btn_copy.onClick {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("link", getModel<FileData>().shortUrl!!)
                clipboard.setPrimaryClip(clip)
                tools.showSnackBar(binding.rv, "Copy Success")
            }
            R.id.btn_download.onClick {
                viewModel.downloadFile(getModel<FileData>().fullUrl!!)
            }
        }
        binding.rv.models = viewModel.getFileList()
        viewModel.upFileData.observe(this, Observer {
            binding.rv.addModels(listOf(viewModel.upFileData.value))
            binding.rv.scrollToPosition(binding.rv.adapter!!.itemCount - 1)
        })
        viewModel.dlFileData.observe(this, Observer {
            tools.showSnackBar(binding.rv, "Download")
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