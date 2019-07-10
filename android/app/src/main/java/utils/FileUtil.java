package utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class FileUtil {

    private static String mBaseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EContract";

    public static boolean isExternalStorageWritable() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }

    public static void makeBaseDirIfNotExsists() {
        File file = new File(mBaseDir);
        if (!file.exists()) {
            file.mkdir();
            Log.v(TAG, "已创建项目目录文件：" + mBaseDir);
        }
    }

    public static File getUserDir(String userName) {
        makeBaseDirIfNotExsists();
        File userDir = new File(mBaseDir + "/" + userName);
        if (!userDir.exists()) {
            userDir.mkdir();
            Log.v(TAG, "已创建用户目录文件：" + mBaseDir + "/" + userName);
        }
        return userDir;
    }

    public static String readFile(String userId) {
        File userDir = getUserDir(userId);
        File file = new File(userDir, userId + ".ekey");
        FileInputStream fileInputStream = null;
        String content = "";
        try {
            fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            content = new String(bytes);
            Log.v(TAG, "从已有的文件" + file.toString() + "读取到的内容是：");
            Log.v(TAG, content);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        return content;
    }

    public static void writeFile(String userId, String content) {
        File userDir = getUserDir(userId);
        File file = new File(userDir, userId + ".ekey");
        FileOutputStream fileOutputStream = null;
        try {
            if (isExternalStorageWritable()) {
                if (!file.exists()) {
                    file.createNewFile();
                    Log.v(TAG, "已创建新文件：" + file.toString());
                } else {
                    Log.v(TAG, file.toString() + "是一个老文件");
                }
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(content.getBytes());
                fileOutputStream.flush();
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
