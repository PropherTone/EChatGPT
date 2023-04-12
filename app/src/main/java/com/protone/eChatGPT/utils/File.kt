package com.protone.eChatGPT.utils

import java.io.File

fun String.saveToFile(dir: String, fileName: String, deleteExitOrAppend: Boolean = true): String? {
    val fileDir = File(dir)
    if (!fileDir.isDirectory) return null
    return fileDir.takeIf { if (!it.exists()) it.mkdirs() else true }?.let { dirFile ->
        var path = "$dir${File.separator}$fileName"
        path += dirFile.listFiles()?.let { listFiles ->
            "_" + listFiles.count { childFile ->
                childFile.path == path
            }
        }
        val file = File(path)
        if (file.exists() && deleteExitOrAppend) file.delete()
        file.takeIf { it.createNewFile() }?.let {
            if (!deleteExitOrAppend) it.appendText(this)
            else it.writeText(this)
            path
        }
    }
}

fun String.getFileContent() = File(this).let {
    if (it.isFile && it.exists()) {
        it.readText()
    } else null
}