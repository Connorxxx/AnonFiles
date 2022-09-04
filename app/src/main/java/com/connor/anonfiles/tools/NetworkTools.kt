package com.connor.anonfiles.tools

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

object NetworkTools {
    fun Uri.toRequestBody(context: Context): RequestBody {
        val document = DocumentFile.fromSingleUri(context, this)
        val length = document?.length() ?: 0
        val fileName = document?.name
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileName)

        val stream = context.contentResolver.openInputStream(this)

        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
                    ?.toMediaTypeOrNull()
            }

            override fun contentLength() = length

            override fun writeTo(sink: BufferedSink) {
                stream?.source()?.use {
                    sink.writeAll(it)
                }
            }
        }
    }
}