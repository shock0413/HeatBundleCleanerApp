package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Application;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class LogManager {
    private static Logger instance;

    private LogManager() {

    }

    public static void update() {
        instance = Logger.getLogger("");
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String time = format.format(date);
        init("info", time + ".txt");
    }

    private static void init(String compile_level, String filename) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download"; //내장에 만든다
        String directoryName = "Portable Auto Cleaner";
        File file = new File(root + "/" + directoryName + "/logs/" + filename);

        if (file.exists()) {
            try {
                LogConfigurator configurator = new LogConfigurator();
                configurator.setFileName(file.getAbsolutePath());
                //      ，
                if (compile_level.equals("debug"))
                    //debug
                    configurator.setRootLevel(Level.DEBUG);
                else
                    //info
                    configurator.setRootLevel(Level.INFO);

                configurator.setLevel("org.apache", Level.ERROR);
                //%p
                //%c
                //%m%n
                configurator.setFilePattern("%d %-5p %m%n");
                configurator.setMaxFileSize(1024 * 1024 * 1);
                configurator.setMaxBackupSize(3);
                configurator.setImmediateFlush(true);
                //      ，  configure
                configurator.configure();
            } catch (Exception e) {
                Log.e(TAG, "[LogManager] [init] " + e.getMessage());
            }
        } else {
            boolean result = false;

            while (!result) {
                try {
                    result = file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file, false);

                    if (result) {
                        Log.i("LogManager", "[init] " + file.getAbsolutePath() + " 파일 생성 완료");
                        LogManager.update();
                    } else {
                        Log.e("LogManager", "[init] " + file.getAbsolutePath() + " 파일 생성 실패");
                    }
                } catch (Exception e) {
                    Log.e("LogManager", "[init] " + file.getAbsolutePath() + " 파일 생성 에러 : " + e.getMessage());
                }
            }
        }
    }

    public static void d(String msg) {
        try {
            instance.debug(msg);
        } catch (Exception e) {
            update();
        }
    }

    public static void i(String msg) {
        try {
            instance.info(msg);
        } catch (Exception e) {
            update();
        }
    }

    public static void e(String msg) {
        try {
            instance.error(msg);
        } catch (Exception e) {
            update();
        }
    }
}
