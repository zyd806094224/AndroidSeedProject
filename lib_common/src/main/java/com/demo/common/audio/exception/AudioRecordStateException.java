package com.demo.common.audio.exception;

/**
 * @Description:
 * @Date: 2024/1/10 11:08
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioRecordStateException extends AudioException {
    @Override
    public int getCode() {
        return 1006;
    }

    @Override
    public String getMsg() {
        return "录制状态异常";
    }
}
