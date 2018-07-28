package com.thetechnocafe.gurleensethi.kotlinfileexplorer.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.FileProvider
import android.util.Log
import com.thetechnocafe.gurleensethi.kotlinfileexplorer.common.FileType
import com.thetechnocafe.gurleensethi.kotlinfileexplorer.models.FileModel
import java.io.File

fun getFileModelsFromFiles(files: List<File>): List<FileModel> {
    return files.map {
        FileModel(it.path, FileType.getFileType(it), it.name, convertFileSizeToMB(it.length()), it.extension, it.listFiles()?.size
                ?: 0)
    }
}

fun getFilesFromPath(path: String, showHiddenFiles: Boolean = false): List<File> {
    val file = File(path)
    return file.listFiles().filter { showHiddenFiles || !it.name.startsWith(".") }.toList()
}

fun Context.launchFileIntent(fileModel: FileModel) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = FileProvider.getUriForFile(this, packageName, File(fileModel.path))
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    startActivity(Intent.createChooser(intent, "Select Application"))
}

fun convertFileSizeToMB(sizeInBytes: Long): Double {
    return (sizeInBytes.toDouble()) / (1024 * 1024)
}

fun createNewFile(fileName: String, path: String, callback: (result:Boolean, message: String) -> Unit){
    val fileAlreadyExists = File(path).listFiles().map { it.name }.contains(fileName)
    if (fileAlreadyExists) {
        callback(false, "'${fileName}' already exists.")
    } else {
        val file = File(path, fileName)
        try {
            val result = file.createNewFile()
            if (result) {
                callback(result, "File '${fileName}' created successfully.")
            } else {
                callback(result, "Unable to create file '${fileName}'.")
            }
        } catch (e: Exception) {
            callback(false, "Unable to create file. Please try again.")
            e.printStackTrace()
        }
    }
}