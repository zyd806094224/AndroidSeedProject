package com.demo.common.audio.exception;

/**
 * @Description:
 * @Date: 2024/1/10 11:03
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioRecordAndroid7ResumeException extends AudioException {
    @Override
    public int getCode() {
        return 1009;
    }

    @Override
    public String getMsg() {
        return "安卓7.0以下版本不支持录音恢复能力异常";
    }
}
