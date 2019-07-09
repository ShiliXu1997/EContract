package com.example.android;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class FileUtil {
    public static boolean isExternalStorageWritable() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }

    public static void readFile(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
//        FileInputStream fileInputStream = null;
//        try {
//            fileInputStream = new FileInputStream(file);
//            byte[] bytes = new byte[fileInputStream.available()];
//            fileInputStream.read(bytes);
//            Log.v(TAG, new String(bytes));
//        } catch (FileNotFoundException fileNotFoundException) {
//            fileNotFoundException.printStackTrace();
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        } finally {
//            if (fileInputStream != null) {
//                try {
//                    fileInputStream.close();
//                } catch (IOException ioException) {
//                    ioException.printStackTrace();
//                }
//            }
//        }
    }

    public static void writeFile(String fileName, String content) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        FileOutputStream fileOutputStream = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.v(TAG, file.toString());
                fileOutputStream = new FileOutputStream(file);
                byte[] bytes = content.getBytes();
                Log.v(TAG, new String(bytes));
                fileOutputStream.write(bytes);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
