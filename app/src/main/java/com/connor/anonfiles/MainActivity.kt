package com.connor.anonfiles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.connor.anonfiles.databinding.ActivityMainBinding
import com.connor.anonfiles.databinding.DialogBottomSheetBinding
import com.connor.anonfiles.databinding.DialogDetailsBinding
import com.connor.anonfiles.model.SortBy
import com.connor.anonfiles.model.room.FileData
import com.connor.anonfiles.tools.VTools
import com.connor.anonfiles.tools.showSnackBar
import com.connor.anonfiles.viewmodel.MainViewModel
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineActivity
import com.drake.serialize.serialize.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : EngineActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModel()
    private val tools: VTools by inject()

    private val sortBy = SortBy()

    private var dataSortBy: SortBy by serial()

    private var name: String by serialLazy("Name", "sortBy")


    override fun initView() {
        binding.v = this

        setSupportActionBar(binding.toolbarMain)
        initRV()
        when (name) {
            "Name" -> viewModel.getFileDatabaseByName.observe(this, Observer {
                binding.rv.models = it
            })
            "Last Add" -> viewModel.getFileDatabase.observe(this, Observer {
                binding.rv.models = it
            })
            "Size" -> viewModel.getFileDatabaseBySize.observe(this, Observer {
                binding.rv.models = it
            })
        }

        //binding.rv.models = viewModel.getFileList()
        viewModel.upFileData.observe(this, Observer {
            //binding.rv.addModels(listOf(viewModel.upFileData.value))
            binding.rv.smoothScrollToPosition(binding.rv.adapter!!.itemCount - 1)
        })
        viewModel.dlFileData.observe(this, Observer {
            binding.rv.showSnackBar("Download")
        })
    }

    override fun initData() {
        dataSortBy = sortBy
        Log.d("initData", "initData: ${sortBy.sortBy}  or ${dataSortBy.sortBy}")
        //viewModel.getFileDatabase()
        viewModel.setupSimpleStorage(this)
    }

    override fun onClick(v: View) {
        when (v) {
            binding.fabUpload -> {
                viewModel.openFilePicker()
            }
        }
    }

    private fun initRV() {
        binding.rv.setup {
            addType<FileData>(R.layout.item_file_list)
            addType<SortBy>(R.layout.litem_file_list_header)
            onBind {
                findView<TextView>(R.id.tv_sort_by)?.text = name

            }
            itemTouchHelper = ItemTouchHelper(object : DefaultItemTouchCallback() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    val fileId = (viewHolder as BindingAdapter.BindingViewHolder)
                        .getModel<FileData>().fileID
                    viewModel.deleteFileDatabase(fileId!!)
                }
            })
            R.id.tv_sort_by.onClick {
                val bottomSheetDialog = BottomSheetDialog(this@MainActivity)
                val bindingSheet = DataBindingUtil.inflate<DialogBottomSheetBinding>(
                    layoutInflater,
                    R.layout.dialog_bottom_sheet,
                    null,
                    false
                )
                bottomSheetDialog.setContentView(bindingSheet.root)
                when (name) {
                    "Name" -> bindingSheet.cardByName.setCardBackgroundColor(
                        ContextCompat.getColor(this@MainActivity, R.color.fab_color))
                    "Last Add" -> bindingSheet.cardByLastAdd.setCardBackgroundColor(
                        ContextCompat.getColor(this@MainActivity, R.color.fab_color))
                    "Size" -> bindingSheet.cardByFileSize.setCardBackgroundColor(
                        ContextCompat.getColor(this@MainActivity, R.color.fab_color))
                }
                bindingSheet.cardByName.setOnClickListener {
                    name = "Name"
                    viewModel.getFileDatabaseByName.observe(this@MainActivity, Observer {
                        binding.rv.models = it
                    })
                    bottomSheetDialog.dismiss()
                }
                bindingSheet.cardByLastAdd.setOnClickListener {
                    name = "Last Add"
                    viewModel.getFileDatabase.observe(this@MainActivity, Observer {
                        binding.rv.models = it
                    })
                    bottomSheetDialog.dismiss()
                }
                bindingSheet.cardByFileSize.setOnClickListener {
                    name = "Size"
                    viewModel.getFileDatabaseBySize.observe(this@MainActivity, Observer {
                        binding.rv.models = it
                    })
                    bottomSheetDialog.dismiss()
                }

                bottomSheetDialog.show()
//                viewModel.getFileDatabaseByName.observe(this@MainActivity, Observer {
//                    binding.rv.models = it
//                })
            }

            R.id.card_fiie_list.onClick {
                val alertDialog = MaterialAlertDialogBuilder(this@MainActivity)
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
                binding.rv.showSnackBar("Copy Success")
            }
            R.id.btn_download.onClick {
                viewModel.downloadFile(getModel<FileData>().fullUrl!!)
            }
        }
        binding.rv.bindingAdapter.addHeader(SortBy(), animation = true)
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) binding.fabUpload.hide() else binding.fabUpload.show()
            }
        })
    }
}