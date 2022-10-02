package com.connor.anonfiles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.anggrayudi.storage.SimpleStorageHelper
import com.connor.anonfiles.databinding.ActivityMainBinding
import com.connor.anonfiles.databinding.DialogBottomSheetBinding
import com.connor.anonfiles.databinding.DialogDetailsBinding
import com.connor.anonfiles.model.SortBy
import com.connor.anonfiles.model.room.FileData
import com.connor.anonfiles.tools.VTools
import com.connor.anonfiles.tools.showSnackBar
import com.connor.anonfiles.ui.SearchActivity
import com.connor.anonfiles.viewmodel.MainViewModel
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineActivity
import com.drake.serialize.serialize.serialLazy
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : EngineActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val TAG = "MainActivity"

    private val viewModel: MainViewModel by viewModel()
    private val tools: VTools by inject()

    private val storageHelper by inject<SimpleStorageHelper> {
        parametersOf(this)
    }

    private var name: String by serialLazy("Name", "sortBy")

    override fun initView() {
        binding.v = this
        setSupportActionBar(binding.toolbarMain)
        initRV()
        when (name) {
            "Name" -> getFileDatabaseByName()
            "Last Add" -> getFileDatabase()
            "Size" -> getFileDatabaseBySize()
        }
    }

    override fun initData() {
        viewModel.setupSimpleStorage(storageHelper) {
            binding.rv.showSnackBar("Done")
            binding.rv.smoothScrollToPosition(0)
        }
    }

    override fun onClick(v: View) {
        when (v) {
            binding.fabUpload -> storageHelper.openFilePicker()
            binding.cardSearch -> tools.startActivity<SearchActivity>(this) {}
        }
    }

    private fun initRV() {
        binding.rv.setup {
            addType<FileData>(R.layout.item_file_list)
            addType<SortBy>(R.layout.litem_file_list_header)
            onBind {
                when (itemViewType) {
                    R.layout.litem_file_list_header ->
                        findView<TextView>(R.id.tv_sort_by).text = name
                }
            }
            itemTouchHelper = viewModel.swiped()
            R.id.tv_sort_by.onClick {
                tools.showBottomSheetDialog<DialogBottomSheetBinding>(
                    R.layout.dialog_bottom_sheet,
                    this@MainActivity,
                    layoutInflater
                ) { bindingSheet, bottomSheetDialog ->
                    when (name) {
                        "Name" -> setCardView(bindingSheet.cardByName)
                        "Last Add" -> setCardView(bindingSheet.cardByLastAdd)
                        "Size" -> setCardView(bindingSheet.cardByFileSize)
                    }
                    bottomDialogClick(bindingSheet, bottomSheetDialog)
                }
            }
            R.id.card_fiie_list.onClick {
                tools.showAlertDialog<DialogDetailsBinding>(R.layout.dialog_details, this@MainActivity, layoutInflater) {
                    it.m = getModel()
                }
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
                viewModel.dlFile(getModel<FileData>().fullUrl!!) {
                    binding.rv.showSnackBar("Downloading")
                }
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

    private fun setCardView(cardView: CardView) {
        cardView.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.fab_color)
        )
    }

    private fun bottomDialogClick(
        bindingSheet: DialogBottomSheetBinding,
        bottomSheetDialog: BottomSheetDialog
    ) {
        bindingSheet.cardByName.setOnClickListener {
            name = "Name"
            getFileDatabaseByName()
            bottomSheetDialog.dismiss()
        }
        bindingSheet.cardByLastAdd.setOnClickListener {
            name = "Last Add"
            getFileDatabase()
            bottomSheetDialog.dismiss()
        }
        bindingSheet.cardByFileSize.setOnClickListener {
            name = "Size"
            getFileDatabaseBySize()
            bottomSheetDialog.dismiss()
        }
    }

    private fun getFileDatabaseByName() {
        lifecycleScope.launch {
            viewModel.getFileDatabaseByName.collect {
                binding.rv.models = it
            }
        }
    }

    private fun getFileDatabase() {
        lifecycleScope.launch {
            viewModel.getFileDatabase.collect {
                binding.rv.models = it
            }
        }
    }

    private fun getFileDatabaseBySize() {
        lifecycleScope.launch {
            viewModel.getFileDatabaseBySize.collect {
                binding.rv.models = it
            }
        }
    }
}