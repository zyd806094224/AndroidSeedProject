package com.demo.common.audio.utils;

import android.content.Context;
import android.os.StatFs;
import android.util.Base64;
import android.util.Log;

import com.demo.common.audio.AudioConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @Description:
 * @Date: 2024/1/5 16:57
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioFileUtil {

    private static final String LOG_TAG = "AudioFileUtil";

    public static String generateRecordNameMills() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static File getInternalRecordsDir(Context context) throws FileNotFoundException {
        File dir = getInternalRecordStorageDir(context.getApplicationContext(), AudioConstants.RECORDS_DIR);
        if (dir == null) {
            throw new FileNotFoundException();
        }
        return dir;
    }

    public static long getAvailableSpaceInBytes(Context context, String directoryName) {
        File directory = context.getApplicationContext().getDir(directoryName, Context.MODE_PRIVATE);
        if (directory == null || !directory.exists()) {
            return -1; // 目录不存在或无法访问
        }
        StatFs statFs = new StatFs(directory.getAbsolutePath());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return statFs.getAvailableBytes();
        } else {
            // 在较早的版本中，使用废弃的方法
            return (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        }
    }

    /**
     * 内部录音文件存储路径
     *
     * @param context
     * @param directoryName
     * @return
     */
    public static File getInternalRecordStorageDir(Context context, String directoryName) {
        File recordsDirectory = context.getApplicationContext().getDir(directoryName, Context.MODE_PRIVATE);
        return recordsDirectory;
    }

    public static File createFile(File path, String fileName) {
        if (path != null) {
            createDir(path);
            File file = new File(path, fileName);
            if (!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        Log.i(LOG_TAG, "The file was successfully created! - " + file.getAbsolutePath());
                    } else {
                        Log.i(LOG_TAG, "The file exist! - " + file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to create the file.", e);
                    return null;
                }
            } else {
                Log.e(LOG_TAG, "File already exists!! Please rename file!");
                Log.i(LOG_TAG, "Renaming file");
//				TODO: Find better way to rename file.
                return createFile(path, "1" + fileName);
            }
            if (!file.canWrite()) {
                Log.e(LOG_TAG, "The file can not be written.");
            }
            return file;
        } else {
            return null;
        }
    }

    public static File createDir(File dir) {
        if (dir != null) {
            if (!dir.exists()) {
                try {
                    if (dir.mkdirs()) {
                        Log.d(LOG_TAG, "Dirs are successfully created");
                        return dir;
                    } else {
                        Log.e(LOG_TAG, "Dirs are NOT created! Please check permission write to external storage!");
                    }
                } catch (Exception e) {

                }
            } else {
                Log.d(LOG_TAG, "Dir already exists");
                return dir;
            }
        }
        Log.e(LOG_TAG, "File is null or unable to create dirs");
        return null;
    }

    public static String addExtension(String name, String extension) {
        return name + AudioConstants.EXTENSION_SEPARATOR + extension;
    }

    public static boolean deleteFile(File file) {
        if (deleteRecursivelyDirs(file)) {
            return true;
        }
        Log.e(LOG_TAG, "Failed to delete directory: " + file.getAbsolutePath());
        return false;
    }

    private static boolean deleteRecursivelyDirs(File file) {
        boolean ok = true;
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                String[] children = file.list();
                if (children != null) {
                    for (String child : children) {
                        ok &= deleteRecursivelyDirs(new File(file, child));
                    }
                }
            }
            if (ok && file.delete()) {
                Log.d(LOG_TAG, "File deleted: " + file.getAbsolutePath());
            }
        }
        return ok;
    }

    /**
     * 音频文件转base64
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String convertAudioFileToBase64(File file) throws IOException {
        byte[] audioData = convertFileToByteArray(file);
        // 使用 Base64 进行编码
        return Base64.encodeToString(audioData, Base64.NO_WRAP);
    }

    private static byte[] convertFileToByteArray(File file) throws IOException {
        byte[] buffer = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(buffer);
        }
        return buffer;
    }
}
