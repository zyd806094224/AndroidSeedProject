package com.demo.common.audio.exception;

/**
 * @Description:
 * @Date: 2024/1/10 11:02
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioRecordAndroid7PauseException extends AudioException{

    @Override
    public int getCode() {
        return 1008;
    }

    @Override
    public String getMsg() {
        return "安卓7.0以下版本不支持录音暂停能力异常";
    }
}
