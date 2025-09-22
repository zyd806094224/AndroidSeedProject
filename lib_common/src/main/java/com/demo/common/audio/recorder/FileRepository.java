package com.demo.common.audio.recorder;

import android.content.Context;

import com.demo.common.audio.AudioOutputFormat;

import java.io.File;

/**
 * @Description:
 * @Date: 2024/1/5 17:12
 * @author: zhaoyudong
 * @version: 1.0
 */
public interface FileRepository {

    boolean hasAvailableSpace(Context context);

    File provideRecordFile(AudioOutputFormat outputFormat);

    boolean deleteRecordFile(String path);

    File getPrivateDir(Context context);

    File[] getPrivateDirFiles(Context context);

    boolean deleteAllRecords();


}
