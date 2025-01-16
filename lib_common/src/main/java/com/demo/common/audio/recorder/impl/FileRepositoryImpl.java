package com.demo.common.audio.recorder.impl;

import android.annotation.SuppressLint;
import android.content.Context;

import com.demo.common.audio.AudioConstants;
import com.demo.common.audio.recorder.FileRepository;
import com.demo.common.audio.utils.AudioFileUtil;
import com.wuba.huangye.common.audio.AudioOutputFormat;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @Description:
 * @Date: 2024/1/5 17:24
 * @author: zhaoyudong
 * @version: 1.0
 */
public class FileRepositoryImpl implements FileRepository {

    private File recordDirectory;
    private volatile static FileRepositoryImpl instance;

    private FileRepositoryImpl(Context context) {
        updateRecordingDir(context);
    }

    public static FileRepositoryImpl getInstance(Context context) {
        if (instance == null) {
            synchronized (FileRepositoryImpl.class) {
                if (instance == null) {
                    instance = new FileRepositoryImpl(context.getApplicationContext());
                }
            }
        }
        return instance;
    }


    @SuppressLint("SdCardPath")
    private void updateRecordingDir(Context context) {
        try {
            recordDirectory = AudioFileUtil.getInternalRecordsDir(context.getApplicationContext());
        } catch (FileNotFoundException e) {
            recordDirectory = new File("/data/data/" + context.getApplicationContext().getPackageName() + "/files");
        }
    }

    @Override
    public boolean hasAvailableSpace(Context context) {
        long space = AudioFileUtil.getAvailableSpaceInBytes(context.getApplicationContext(), AudioConstants.RECORDS_DIR);
        double mb = (double) space / (1024 * 1024);
        return mb >= AudioConstants.MIN_AVAILABLE_SPACE;
    }

    @Override
    public File provideRecordFile(AudioOutputFormat outputFormat) {
        File recordFile = null;
        String recordName = AudioFileUtil.generateRecordNameMills();
        if (outputFormat == AudioOutputFormat.AudioOutputFormatAmr) {
            recordFile = AudioFileUtil.createFile(recordDirectory, AudioFileUtil.addExtension(recordName, AudioConstants.FORMAT_AMR));
        } else if (outputFormat == AudioOutputFormat.AudioOutputFormatM4a) {
            recordFile = AudioFileUtil.createFile(recordDirectory, AudioFileUtil.addExtension(recordName, AudioConstants.FORMAT_M4A));
        }
        return recordFile;
    }

    @Override
    public boolean deleteRecordFile(String path) {
        if (path != null) {
            return AudioFileUtil.deleteFile(new File(path));
        }
        return false;
    }

    @Override
    public File getPrivateDir(Context context) {
        try {
            return AudioFileUtil.getInternalRecordsDir(context);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public File[] getPrivateDirFiles(Context context) {
        try {
            return AudioFileUtil.getInternalRecordsDir(context).listFiles();
        } catch (FileNotFoundException e) {
            return new File[]{};
        }
    }

    @Override
    public boolean deleteAllRecords() {
        return AudioFileUtil.deleteFile(recordDirectory);
    }
}
