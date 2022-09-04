package com.connor.anonfiles.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemSwipe

@Entity
data class FileData(
    var fullUrl: String? = "https://anonfiles.com/u1C0ebc4b0/file.txt",
    var shortUrl: String? = "https://anonfiles.com/u1C0ebc4b0",
    var fileID: String? = "u1C0ebc4b0",
    var fileName: String? = "file.txt",
    var fileSize: String? = "6.7 KB",
) : ItemSwipe {
    override var itemOrientationSwipe: Int = ItemOrientation.LEFT
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
