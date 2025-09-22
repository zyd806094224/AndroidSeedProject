package com.demo.common.audio.recorder;

import com.demo.common.audio.exception.AudioException;
import com.demo.common.audio.AudioOutputFormat;

import java.io.File;

/**
 * @Description:
 * @Date: 2024/1/5 10:47
 * @author: zhaoyudong
 * @version: 1.0
 */
public interface RecorderContract {

    interface RecorderCallback {
        void onStartRecord(File output);

        void onPauseRecord();

        void onResumeRecord();

        void onRecordProgress(long mills, int amp); //录制进行时长、最大音频振幅

        void onStopRecord(File output, long duration);

        void onError(AudioException throwable);
    }

    interface Recorder {
        void setRecorderCallback(RecorderCallback callback);

        RecorderCallback getRecorderCallback();

        void startRecording(String outputFile, AudioOutputFormat audioOutputFormat, int channelCount, int sampleRate, int bitrate, int maxRecordingDuration);

        void resumeRecording();

        void pauseRecording();

        void stopRecording();

        void forceStopRecording();

        boolean isRecording();

        boolean isPaused();
    }
}
