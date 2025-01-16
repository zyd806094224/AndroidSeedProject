package com.demo.common.audio.exception;

/**
 * @Description:
 * @Date: 2024/1/11 10:15
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioRecordNoStorageSpaceException extends AudioException{

    @Override
    public int getCode() {
        return 1005;
    }

    @Override
    public String getMsg() {
        return "空间不足";
    }
}
