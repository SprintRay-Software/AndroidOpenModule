package com.sprintray.tools

import android.os.Environment
import android.util.Log
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

object LogTools {
    private val logDirectoryPath = Environment.getExternalStorageDirectory().toString() + "/Logs"
    private val logcatPath = "$logDirectoryPath/logcat.txt"
    private lateinit var onLogCallback: OnLogCallback
    private var onSave = false

    val TAG = "LogTools"

    /**
     * To capture logcat information into a file for debugging purpose.
     *
     * "logcat -d *:V" -> Verbose
     * "logcat -d *:D" -> Debug
     * "logcat -d *:I" -> Info
     * "logcat -d *:W" -> Warning
     * "logcat -d *:E" -> Error
     * */
    fun saveLogcatToFile() {
        if (onSave) return
        onSave = true

        CoroutineScope(Dispatchers.Main).launch {
            var success = withContext(Dispatchers.IO) {
                val logDir = File(logDirectoryPath)
                if (!logDir.exists()) {
                    logDir.mkdir()
                    delay(10)
                }
                val logFile = File(logcatPath)
                logFile.delete()
                delay(10)
                if (!logFile.exists()) {
                    logFile.createNewFile()
                }

                try {
                    val command = "logcat -d *:I"
                    val process = Runtime.getRuntime().exec(command)
                    val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        if (line.isNullOrEmpty()) continue
                        if (!line!!.contains("Request")) {
                            logFile.appendText("$line\n")
                        }
                    }
                    process.destroy();
                    delay(10)
                } catch (e: IOException) {
                    onSave = false
                    e.printStackTrace()
                }
                "success"
            }
            Log.i(TAG, "saveLogcat: $success")
            if(success.equals("success")) onLogCallback.onSuccess(logcatPath)
            onSave = false
        }
    }

    interface OnLogCallback{
        fun onSuccess(path :String);
    }

    fun setOnLogCallback(callback: OnLogCallback){
        this.onLogCallback = callback
    }
}