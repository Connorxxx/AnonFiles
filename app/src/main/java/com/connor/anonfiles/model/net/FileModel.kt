package com.connor.anonfiles.model.net

import kotlinx.serialization.Serializable

@Serializable
data class FileModel(
    val data: Data,
    val status: Boolean
) {
    @Serializable
    data class Data(
        val file: File
    ) {
        @Serializable
        data class File(
            val metadata: Metadata,
            val url: Url
        ) {
            @Serializable
            data class Metadata(
                val id: String,
                val name: String,
                val size: Size
            ) {
                @Serializable
                data class Size(
                    val bytes: Int,
                    val readable: String
                )
            }

            @Serializable
            data class Url(
                val full: String,
                val short: String
            )
        }
    }
}