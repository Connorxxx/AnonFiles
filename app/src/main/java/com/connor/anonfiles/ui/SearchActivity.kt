package com.connor.anonfiles.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.connor.anonfiles.R
import com.connor.anonfiles.databinding.ActivitySearchBinding
import com.connor.anonfiles.model.room.FileData
import com.connor.anonfiles.tools.showSnackBar
import com.connor.anonfiles.viewmodel.MainViewModel
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineActivity
import com.drake.engine.base.EngineToolbarActivity
import com.drake.engine.databinding.bind
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : EngineToolbarActivity<ActivitySearchBinding>(R.layout.activity_search) {

    private val viewModel: MainViewModel by viewModel()

    lateinit var editText: EditText

    override fun initView() {
        initRV()
        editText = findViewById(R.id.etSearch)
        val imm = editText.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        editText.apply {
            requestFocus()
            imm.showSoftInput(editText, 0)
        }
        editText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && textView.text.isNotBlank()) {
                viewModel.queryName(editText.text.toString())
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
            } else {
                editText.showSnackBar("Please Input")
            }
            return@setOnEditorActionListener true
        }
        viewModel.getFileDatabaseByQueryName.observe(this) {
            binding.rvSearch.models = it
            Log.d("TAG", "initView: $it")
        }
    }

    override fun initData() {

    }

    private fun initRV() {
        binding.rvSearch.setup {
            addType<FileData>(R.layout.item_file_list)
        }
    }

}