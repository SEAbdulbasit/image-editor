package com.example.stickermaker

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class FilesManager {

    private val PARENT_DIR_NAME = "parent_folder"

    fun getParentDirectory(context: Context): File {
        val parentDir = File(context.filesDir, PARENT_DIR_NAME)
        if (!parentDir.exists()) {
            parentDir.mkdir()
        }
        return parentDir
    }

    fun getDirectories(context: Context): List<String> {
        val parentDir = getParentDirectory(context)
        val dirs = parentDir.listFiles()?.filter { it.isDirectory }
        return dirs?.map { it.name } ?: emptyList()
    }

    fun createDirectory(directoryName: String, parentDir: File) {
        val newDir = File(parentDir, directoryName)
        newDir.mkdir()
    }

    fun getItemsForDirectory(directoryName: String, context: Context): List<File> {
        val parentDirectory = getParentDirectory(context)
        val directory = File(parentDirectory, directoryName)
        return directory.listFiles()?.toList() ?: emptyList()
    }

    fun saveImageAsWebp(bitmap: Bitmap, directory: File, fileName: String) {
        val file = File(directory, "$fileName.webp")
        FileOutputStream(file).use { out ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, out)
            }
            else{
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out)
            }
            out.flush()
            out.close()
        }
    }


}