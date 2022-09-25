package com.connor.anonfiles.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.connor.anonfiles.R
import com.connor.anonfiles.databinding.DialogDetailsBinding
import com.connor.anonfiles.model.room.FileData
import com.drake.engine.utils.DeviceUtils.getModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class VTools {

    inline fun <reified T> startActivity(context: Context, block: Intent.() -> Unit) {
        val intent = Intent(context, T::class.java)
        intent.block()
        context.startActivity(intent)
    }

    inline fun <reified T> startService(context: Context, block: Intent.() -> Unit) {
        val intent = Intent(context, T::class.java)
        intent.block()
        context.startService(intent)
    }

    inline fun <reified T> stopService(context: Context, block: Intent.() -> Unit) {
        val stopService = Intent(context, T::class.java)
        stopService.block()
        context.stopService(stopService)
    }

    inline fun showAlertDialog(context: Context, layoutInflater: LayoutInflater, block: (binding: DialogDetailsBinding) -> Unit) {
        val alertDialog = MaterialAlertDialogBuilder(context)
        val binding = DataBindingUtil.inflate<DialogDetailsBinding>(
            layoutInflater,
            R.layout.dialog_details,
            null,
            false
        )
        block(binding)
        alertDialog.setView(binding.root)
        alertDialog.show()
    }

    fun openLink(link: String, context: Context, view: View) {
        val sourceUri = link.toUri()
        if (sourceUri.toString().startsWith("http")) {
            val openURI = sourceUri.toString()
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, openURI.toUri())
        } else {
            Snackbar.make(view, "this $link is not a URL", Snackbar.LENGTH_LONG).show()
        }
    }

    fun shareLink(source: String, context: Context, view: View) {
        if (source.startsWith("http")) {
            val sharedIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, source)
                type = "text/*"
            }, null)
            context.startActivity(sharedIntent)
        } else {
            Snackbar.make(view, "this $source is not a URL", Snackbar.LENGTH_LONG).show()
        }
    }
}