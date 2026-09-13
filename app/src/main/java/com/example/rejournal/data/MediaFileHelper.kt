package com.example.rejournal.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

object MediaFileHelper {
    const val MAX_PHOTOS = 8
    const val MAX_AUDIO_MEMOS = 8

    private const val PHOTOS_DIR = "photos"
    private const val AUDIO_DIR = "audio"


    fun createPhotoFile(context: Context): Pair<File, Uri> {
        val dir = File(context.filesDir, PHOTOS_DIR).apply { mkdirs() }
        val file = File(dir, "photo_${UUID.randomUUID()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return file to uri
    }

    fun createAudioFile(context: Context): File {
        val dir = File(context.filesDir, AUDIO_DIR).apply { mkdirs() }
        return File(dir, "memo_${UUID.randomUUID()}.m4a")
    }

    fun deleteFile(path: String) {
        val file = File(path)
        if (file.exists()) file.delete()
    }
}