package com.readymon.objectdetectionapp.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object FileStorageHelper {
    fun saveBitmap(context: Context, bitmap: Bitmap): String {
        val file = File(context.filesDir, "img_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return file.absolutePath
    }
}
