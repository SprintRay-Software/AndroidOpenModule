package com.sprintray.net.utils;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ShellUtils;

import java.io.File;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class IUtils {
    private static final String TAG = "Utils";


    public static String getPrintID() {

        ShellUtils.CommandResult commandResult = ShellUtils.execCmd("cat /data/data/serialnumber", true);

        Log.d(TAG, "getPrintID: " + commandResult.successMsg);
        Log.d(TAG, "getPrintID: " + commandResult.errorMsg);
        Log.d(TAG, "getPrintID: " + commandResult.result);

        if (TextUtils.isEmpty(commandResult.successMsg)) {
            return "Capricorn001";
        } else {
            return commandResult.successMsg;
        }

    }


    /**
     * read content form file
     *
     * @param FileName
     * @return
     */
    public static String readMainVersion(String FileName, String key, String defaultVersion) {
        File file = new File(FileName);
        String result = defaultVersion;
        //文件是否存在
        if (!file.exists()) {
            return defaultVersion;
        }
        try {
            //获取source对象
            Source read = Okio.source(file);
            BufferedSource bufferedSource = Okio.buffer(read);
            result = bufferedSource.readUtf8Line();
            //关闭source
            bufferedSource.close();
            read.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (result==null || result.isEmpty()){
            result = defaultVersion;
        }


        return result;
    }

    public static void saveContent(String fileName, String content) {
        try {
            File file = new File(fileName);
            //文件是否存在，不存在就创建
            if (!file.exists()) {
                file.createNewFile();
            }
            //获取sink对象
            Sink write = Okio.sink(file);
            //获取sink缓冲对象
            BufferedSink bufferedSink = Okio.buffer(write);
            //写入数据
            bufferedSink.writeUtf8(content);
            //关闭sink
            bufferedSink.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
