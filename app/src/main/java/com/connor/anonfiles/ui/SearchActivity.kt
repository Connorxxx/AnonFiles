package com.connor.anonfiles.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.connor.anonfiles.R
import com.connor.anonfiles.databinding.ActivitySearchBinding
import com.connor.anonfiles.model.room.FileData
import com.connor.anonfiles.tools.VTools
import com.connor.anonfiles.tools.showSnackBar
import com.connor.anonfiles.viewmodel.MainViewModel
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineToolbarActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : EngineToolbarActivity<ActivitySearchBinding>(R.layout.activity_search) {

    private val viewModel: MainViewModel by viewModel()
    private val tools: VTools by inject()

    private lateinit var editText: EditText

    override fun initView() {
        initRV()
        editText = findViewById(R.id.etSearch)
        val imm = editText.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        editText.apply {
            requestFocus()
            imm.showSoftInput(editText, 0)
        }
        editText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && textView.text.isNotBlank()) {
                lifecycleScope.launch {
                    viewModel.getFileDatabaseByQueryName(editText.text.toString()).collect { list ->
                        list.forEach { it.itemOrientationSwipe = ItemOrientation.NONE }
                        withContext(Dispatchers.Main) {
                            binding.rvSearch.models = list
                        }
                    }
                }
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
            } else {
                editText.showSnackBar("Please Input")
            }
            return@setOnEditorActionListener true
        }
    }

    override fun initData() {

    }

    private fun initRV() {
        binding.rvSearch.setup {
            addType<FileData>(R.layout.item_file_list)
            R.id.card_fiie_list.onClick {
                tools.showAlertDialog(this@SearchActivity, layoutInflater) {
                    it.m = getModel()
                }
            }
            R.id.img_share.onClick {
                tools.shareLink(getModel<FileData>().shortUrl!!, this@SearchActivity, binding.rvSearch)
            }
            R.id.btn_copy.onClick {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("link", getModel<FileData>().shortUrl!!)
                clipboard.setPrimaryClip(clip)
                binding.rvSearch.showSnackBar("Copy Success")
            }
            R.id.btn_download.onClick {
               viewModel.dlFile(getModel<FileData>().fullUrl!!) {
                   binding.rvSearch.showSnackBar("Downloading")
               }
                Log.d("TAG", "initRV: ${getModel<FileData>().fullUrl!!}")
            }
        }
    }

}