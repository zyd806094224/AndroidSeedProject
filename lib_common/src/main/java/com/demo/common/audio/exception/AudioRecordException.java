package com.demo.common.audio.exception;

/**
 * @Description:
 * @Date: 2024/1/11 10:44
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioRecordException extends AudioException{
    @Override
    public int getCode() {
        return 1001;
    }

    @Override
    public String getMsg() {
        return "录制异常";
    }
}
