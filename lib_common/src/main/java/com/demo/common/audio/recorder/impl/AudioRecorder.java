package com.demo.common.audio.recorder.impl;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.demo.common.audio.AudioConstants;
import com.demo.common.audio.exception.AudioRecordAndroid7PauseException;
import com.demo.common.audio.exception.AudioRecordAndroid7ResumeException;
import com.demo.common.audio.exception.AudioRecordCreateFileException;
import com.demo.common.audio.exception.AudioRecordException;
import com.demo.common.audio.exception.AudioRecordStateException;
import com.demo.common.audio.recorder.RecorderContract;
import com.demo.common.audio.AudioOutputFormat;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description:
 * @Date: 2024/1/5 10:51
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioRecorder implements RecorderContract.Recorder {

    private MediaRecorder recorder = null;
    private File recordFile = null;
    private long updateTime = 0;
    /**
     * 已录音时长
     */
    private long durationMills = 0;
    /**
     * 可用的录音时长
     */
    private long availableRecordingDuration = AudioConstants.MAX_RECORDING_DURATION;
    private final AtomicBoolean isRecording = new AtomicBoolean(false);
    private final AtomicBoolean isPaused = new AtomicBoolean(false);
    private RecorderContract.RecorderCallback recorderCallback;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private static class RecorderSingletonHolder {
        private static final AudioRecorder singleton = new AudioRecorder();

        public static AudioRecorder getSingleton() {
            return RecorderSingletonHolder.singleton;
        }
    }

    public static AudioRecorder getInstance() {
        return RecorderSingletonHolder.getSingleton();
    }

    private AudioRecorder() {
    }

    @Override
    public void setRecorderCallback(RecorderContract.RecorderCallback callback) {
        this.recorderCallback = callback;
    }

    @Override
    public RecorderContract.RecorderCallback getRecorderCallback() {
        return this.recorderCallback;
    }

    @Override
    public void startRecording(String outputFile, AudioOutputFormat audioOutputFormat, int channelCount, int sampleRate, int bitrate, int maxRecordingDuration) {
        if (maxRecordingDuration > 0) {
            availableRecordingDuration = maxRecordingDuration * 1000L;
        }
        recordFile = new File(outputFile);
        if (recordFile.exists() && recordFile.isFile()) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            if (audioOutputFormat == AudioOutputFormat.AudioOutputFormatAmr) {
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            } else if (audioOutputFormat == AudioOutputFormat.AudioOutputFormatM4a) {
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            }
            recorder.setAudioChannels(channelCount);
            recorder.setAudioSamplingRate(sampleRate);
            recorder.setAudioEncodingBitRate(bitrate);
            recorder.setMaxDuration(-1);
            recorder.setOutputFile(recordFile.getAbsolutePath());
            try {
                recorder.prepare();
                recorder.start();
                updateTime = System.currentTimeMillis();
                isRecording.set(true);
                scheduleRecordingTimeUpdate();
                dealMaxRecordingDurationShutdown();
                if (recorderCallback != null) {
                    recorderCallback.onStartRecord(recordFile);
                }
                isPaused.set(false);
            } catch (IOException | IllegalStateException e) {
                if (recorderCallback != null) {
                    recorderCallback.onError(new AudioRecordException());
                }
            }
        } else {
            if (recorderCallback != null) {
                recorderCallback.onError(new AudioRecordCreateFileException());
            }
        }
    }

    private void dealMaxRecordingDurationShutdown() {
        handler.postDelayed(() -> {
            stopRecording();
        }, availableRecordingDuration - durationMills);
    }

    private void scheduleRecordingTimeUpdate() {
        handler.postDelayed(() -> {
            if (recorderCallback != null && recorder != null) {
                try {
                    long curTime = System.currentTimeMillis();
                    durationMills += curTime - updateTime;
                    updateTime = curTime;
                    recorderCallback.onRecordProgress(durationMills, recorder.getMaxAmplitude());
                } catch (IllegalStateException e) {

                }
                scheduleRecordingTimeUpdate();
            }
        }, AudioConstants.RECORDING_VISUALIZATION_INTERVAL);
    }

    private void stopRecordingTimer() {
        handler.removeCallbacksAndMessages(null);
        updateTime = 0;
    }

    private void pauseRecordingTimer() {
        handler.removeCallbacksAndMessages(null);
        updateTime = 0;
    }

    @Override
    public void resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isPaused.get()) {
                try {
                    recorder.resume();
                    updateTime = System.currentTimeMillis();
                    scheduleRecordingTimeUpdate();
                    dealMaxRecordingDurationShutdown();
                    if (recorderCallback != null) {
                        recorderCallback.onResumeRecord();
                    }
                    isPaused.set(false);
                } catch (IllegalStateException e) {
                    if (recorderCallback != null) {
                        recorderCallback.onError(new AudioRecordException());
                    }
                }
            } else {
                if (recorderCallback != null) {
                    recorderCallback.onError(new AudioRecordStateException());
                }
            }
        } else {//低于7.0版本 不支持录音暂停恢复功能
            if (recorderCallback != null) {
                recorderCallback.onError(new AudioRecordAndroid7ResumeException());
            }
        }
    }

    @Override
    public void pauseRecording() {
        if (isRecording.get()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!isPaused.get()) {
                    try {
                        recorder.pause();
                        durationMills += System.currentTimeMillis() - updateTime;
                        pauseRecordingTimer();
                        if (recorderCallback != null) {
                            recorderCallback.onPauseRecord();
                        }
                        isPaused.set(true);
                    } catch (IllegalStateException e) {
                        if (recorderCallback != null) {
                            recorderCallback.onError(new AudioRecordException());
                        }
                    }
                }
            } else {//低于7.0版本暂停直接就终止录音了
                if (recorderCallback != null) {
                    recorderCallback.onError(new AudioRecordAndroid7PauseException());
                }
            }
        } else { //非正在录音状态不支持暂停录音功能
            if (recorderCallback != null) {
                recorderCallback.onError(new AudioRecordStateException());
            }
        }
    }

    @Override
    public void stopRecording() {
        if (isRecording.get()) {
            stopRecordingTimer();
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                if (recorderCallback != null) {
                    recorderCallback.onError(new AudioRecordException());
                }
            }
            recorder.release();
            if (recorderCallback != null) {
                recorderCallback.onStopRecord(recordFile, durationMills);
            }
            durationMills = 0;
            availableRecordingDuration = AudioConstants.MAX_RECORDING_DURATION;
            recordFile = null;
            isRecording.set(false);
            isPaused.set(false);
            recorder = null;
        } else {
            if (recorderCallback != null) {
                recorderCallback.onError(new AudioRecordStateException());
            }
        }
    }

    @Override
    public void forceStopRecording() {
        if (isRecording.get()) {
            stopRecordingTimer();
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                /*if (recorderCallback != null) {
                    recorderCallback.onError(new AudioRecordException());
                }*/
            }
            recorder.release();
            /*if (recorderCallback != null) {
                recorderCallback.onError(new AudioRecordException());
            }*/
            durationMills = 0;
            availableRecordingDuration = AudioConstants.MAX_RECORDING_DURATION;
            recordFile = null;
            isRecording.set(false);
            isPaused.set(false);
            recorder = null;
        }
    }

    @Override
    public boolean isRecording() {
        return isRecording.get();
    }

    @Override
    public boolean isPaused() {
        return isPaused.get();
    }
}
